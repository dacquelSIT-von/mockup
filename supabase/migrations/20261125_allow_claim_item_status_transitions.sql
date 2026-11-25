create or replace function public.protect_item_moderation_fields()
returns trigger
language plpgsql
security definer
set search_path = ''
as $function$
begin
    if auth.uid() is not null then
        if new.user_id is distinct from old.user_id then
            raise exception 'You cannot change protected item fields.';
        end if;

        if new.status is distinct from old.status then
            if new.status = 'CLAIM_PENDING'
               and old.status = 'AVAILABLE'
               and exists (
                   select 1
                   from public.claims c
                   where c.item_id = old.id
                     and c.claimant_id = auth.uid()
                     and c.status = 'PENDING_REVIEW'
               ) then
                return new;
            end if;

            if new.status = 'RETURNED'
               and old.status = 'CLAIM_PENDING'
               and exists (
                   select 1
                   from public.claims c
                   where c.item_id = old.id
                     and c.status = 'APPROVED'
               ) then
                return new;
            end if;

            raise exception 'You cannot change protected item fields.';
        end if;
    end if;

    return new;
end;
$function$;
