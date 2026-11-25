create or replace function public.submit_claim(
    p_item_id uuid,
    p_claim_description text
)
returns uuid
language plpgsql
security definer
set search_path = ''
as $$
declare
    v_claim_id uuid;
begin
    if auth.uid() is null then
        raise exception 'You must be logged in to submit a claim.';
    end if;

    insert into public.claims (
        item_id,
        claimant_id,
        claim_description,
        status
    )
    values (
        p_item_id,
        auth.uid(),
        p_claim_description,
        'PENDING_REVIEW'
    )
    returning id into v_claim_id;

    update public.items
    set status = 'CLAIM_PENDING'
    where id = p_item_id
      and status = 'AVAILABLE';

    if not found then
        raise exception 'This item is no longer available to claim.';
    end if;

    return v_claim_id;
end;
$$;

revoke all on function public.submit_claim(uuid, text) from public;
grant execute on function public.submit_claim(uuid, text) to authenticated;

notify pgrst, 'reload schema';
