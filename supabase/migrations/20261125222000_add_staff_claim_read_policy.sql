-- Allow authenticated users to read their own claims and staff to read all claims.
CREATE POLICY "Users and staff can view claims"
ON public.claims
FOR SELECT
TO authenticated
USING (
    claimant_id = (SELECT auth.uid())
    OR (SELECT public.is_staff())
);
