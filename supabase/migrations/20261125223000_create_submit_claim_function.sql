-- Atomically marks an available item as claim-pending and records the
-- authenticated user's claim. The caller cannot choose the claimant ID.
-- SECURITY DEFINER allows this controlled operation to perform both writes;
-- the empty search_path and qualified object names prevent object shadowing.
CREATE OR REPLACE FUNCTION public.submit_claim(
    p_item_id UUID,
    p_claim_reason TEXT,
    p_identifying_details TEXT
)
RETURNS public.claims
LANGUAGE plpgsql
SECURITY DEFINER
SET search_path = ''
AS $$
DECLARE
    v_user_id UUID;
    v_updated_item_id UUID;
    v_claim public.claims;
BEGIN
    v_user_id := auth.uid();
    IF v_user_id IS NULL THEN
        RAISE EXCEPTION 'Authentication is required to submit a claim.';
    END IF;

    IF NULLIF(btrim(p_claim_reason), '') IS NULL THEN
        RAISE EXCEPTION 'Claim reason must not be blank.';
    END IF;

    IF NULLIF(btrim(p_identifying_details), '') IS NULL THEN
        RAISE EXCEPTION 'Identifying details must not be blank.';
    END IF;

    UPDATE public.items AS item
    SET status = 'CLAIM_PENDING'
    WHERE item.id = p_item_id
      AND item.status = 'AVAILABLE'
    RETURNING item.id INTO v_updated_item_id;

    IF v_updated_item_id IS NULL THEN
        RAISE EXCEPTION 'Item is unavailable or does not exist.';
    END IF;

    INSERT INTO public.claims (
        item_id,
        claimant_id,
        claim_reason,
        identifying_details,
        claim_description,
        status
    )
    VALUES (
        p_item_id,
        v_user_id,
        btrim(p_claim_reason),
        btrim(p_identifying_details),
        format(
            'Reason: %s; Identifying details: %s',
            btrim(p_claim_reason),
            btrim(p_identifying_details)
        ),
        'PENDING_REVIEW'
    )
    RETURNING * INTO v_claim;

    RETURN v_claim;
END;
$$;

-- Only authenticated users may invoke this controlled claim submission path.
REVOKE EXECUTE ON FUNCTION public.submit_claim(UUID, TEXT, TEXT) FROM PUBLIC, anon;
GRANT EXECUTE ON FUNCTION public.submit_claim(UUID, TEXT, TEXT) TO authenticated;
