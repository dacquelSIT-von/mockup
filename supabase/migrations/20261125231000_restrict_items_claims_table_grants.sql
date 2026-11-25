-- Remove direct table privileges from anon and authenticated.
REVOKE ALL PRIVILEGES ON TABLE public.items
FROM anon, authenticated;

REVOKE ALL PRIVILEGES ON TABLE public.claims
FROM anon, authenticated;

-- Restore the required item operations for authenticated users.
GRANT SELECT, INSERT, UPDATE, DELETE ON TABLE public.items
TO authenticated;

-- Claims are read directly; submission and review use SECURITY DEFINER RPCs.
GRANT SELECT ON TABLE public.claims
TO authenticated;
