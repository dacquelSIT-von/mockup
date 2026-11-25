-- Allows staff to approve or reject a pending claim as one atomic operation.
-- The function checks the caller's database role; client-side screen access
-- is not used as an authorization boundary.
CREATE OR REPLACE FUNCTION public.review_claim(
    p_claim_id UUID,
    p_decision TEXT
)
RETURNS public.claims
LANGUAGE plpgsql
SECURITY DEFINER
SET search_path = ''
AS $$
DECLARE
    v_item_id UUID;
    v_locked_item_id UUID;
    v_claim public.claims;
BEGIN
    IF auth.uid() IS NULL THEN
        RAISE EXCEPTION 'Authentication is required to review claims.';
    END IF;

    IF NOT public.is_staff() THEN
        RAISE EXCEPTION 'Only staff members may review claims.';
    END IF;

    IF p_decision IS NULL OR p_decision NOT IN ('APPROVE', 'REJECT') THEN
        RAISE EXCEPTION 'Decision must be APPROVE or REJECT.';
    END IF;

    -- Read the item ID first, then lock item before claim. All review calls
    -- for the same item therefore serialize using the same lock order.
    SELECT claim.item_id
    INTO v_item_id
    FROM public.claims AS claim
    WHERE claim.id = p_claim_id;

    IF NOT FOUND THEN
        RAISE EXCEPTION 'Claim % does not exist.', p_claim_id;
    END IF;

    SELECT item.id
    INTO v_locked_item_id
    FROM public.items AS item
    WHERE item.id = v_item_id
    FOR UPDATE;

    IF NOT FOUND THEN
        RAISE EXCEPTION 'Item % for claim % does not exist.', v_item_id, p_claim_id;
    END IF;

    -- Re-read and lock the claim after its item is locked, then validate its
    -- current state so stale or competing decisions cannot be applied.
    SELECT claim.*
    INTO v_claim
    FROM public.claims AS claim
    WHERE claim.id = p_claim_id
    FOR UPDATE;

    IF NOT FOUND THEN
        RAISE EXCEPTION 'Claim % no longer exists.', p_claim_id;
    END IF;

    IF v_claim.item_id IS DISTINCT FROM v_item_id THEN
        RAISE EXCEPTION 'Claim % changed items during review; retry the operation.', p_claim_id;
    END IF;

    IF v_claim.status IS DISTINCT FROM 'PENDING_REVIEW' THEN
        RAISE EXCEPTION 'Claim % is not pending review.', p_claim_id;
    END IF;

    IF p_decision = 'APPROVE' THEN
        UPDATE public.claims AS claim
        SET status = 'APPROVED'
        WHERE claim.id = p_claim_id
          AND claim.status = 'PENDING_REVIEW'
        RETURNING claim.* INTO v_claim;

        UPDATE public.claims AS other_claim
        SET status = 'REJECTED'
        WHERE other_claim.item_id = v_item_id
          AND other_claim.id <> p_claim_id
          AND other_claim.status = 'PENDING_REVIEW';

        UPDATE public.items AS item
        SET status = 'RETURNED'
        WHERE item.id = v_item_id;
    ELSE
        UPDATE public.claims AS claim
        SET status = 'REJECTED'
        WHERE claim.id = p_claim_id
          AND claim.status = 'PENDING_REVIEW'
        RETURNING claim.* INTO v_claim;

        IF EXISTS (
            SELECT 1
            FROM public.claims AS remaining_claim
            WHERE remaining_claim.item_id = v_item_id
              AND remaining_claim.status = 'PENDING_REVIEW'
        ) THEN
            UPDATE public.items AS item
            SET status = 'CLAIM_PENDING'
            WHERE item.id = v_item_id;
        ELSE
            UPDATE public.items AS item
            SET status = 'AVAILABLE'
            WHERE item.id = v_item_id;
        END IF;
    END IF;

    RETURN v_claim;
END;
$$;

-- Allow authenticated callers to invoke the RPC; the function enforces staff role.
REVOKE EXECUTE ON FUNCTION public.review_claim(UUID, TEXT) FROM PUBLIC, anon;
GRANT EXECUTE ON FUNCTION public.review_claim(UUID, TEXT) TO authenticated;
