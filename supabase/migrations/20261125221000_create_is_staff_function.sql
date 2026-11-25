-- Returns whether the currently authenticated user has the STAFF role.
-- This SECURITY DEFINER helper can be used by row-level security policies
-- without granting clients direct access to change or elevate profile roles.
CREATE OR REPLACE FUNCTION public.is_staff()
RETURNS BOOLEAN
LANGUAGE SQL
STABLE
SECURITY DEFINER
SET search_path = ''
AS $$
    SELECT EXISTS (
        SELECT 1
        FROM public.profiles
        WHERE public.profiles.id = auth.uid()
          AND public.profiles.role = 'STAFF'
    );
$$;

-- Limit execution to authenticated application users.
REVOKE EXECUTE ON FUNCTION public.is_staff() FROM PUBLIC;
GRANT EXECUTE ON FUNCTION public.is_staff() TO authenticated;
