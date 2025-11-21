


DELETE FROM [dbo].[TenantSectionDetailOption]

--DBCC CHECKIDENT ('[TenantSectionDetailOption]', RESEED, 0);

--------------------------------------------------
SET IDENTITY_INSERT [dbo].[TenantSectionDetailOption] ON


INSERT [dbo].[TenantSectionDetailOption] ([TenantId], [OptionName], [OptionValue], [Visible], [OptionLabel]) VALUES

(2, N'yes-no', N'true', 1, N'Yes'),
(2, N'yes-no', N'false', 1, N'No'),
(2, N'vatAbsorbedBy', N'BAY & Subs.', 1, N'BAY & Subs.'),
(2, N'vatAbsorbedBy', N'Vendors', 1, N'Vendors'),
(2, N'stampDuty', N'BAY & Subs.', 1, N'BAY & Subs.'),
(2, N'stampDuty', N'Vendors', 1, N'Vendors'),
(2, N'whtAbsorbedBy', N'BAY & Subs.', 1, N'BAY & Subs.'),
(2, N'whtAbsorbedBy', N'Vendors', 1, N'Vendors'),
(2, N'whtAbsorbedBy', N'No', 1, N'No'),
(2, N'whtAbsorbedBy', N'Not Specific', 1, N'Not Specific'),


(4, N'yes-no', N'true', 1, N'Yes'),
(4, N'yes-no', N'false', 1, N'No'),
(4, N'vatAbsorbedBy', N'DEMO & Subs.', 1, N'DEMO & Subs.'),
(4, N'vatAbsorbedBy', N'Vendors', 1, N'Vendors'),
(4, N'stampDuty', N'DEMO & Subs.', 1, N'DEMO & Subs.'),
(4, N'stampDuty', N'Vendors', 1, N'Vendors'),
(4, N'whtAbsorbedBy', N'DEMO & Subs.', 1, N'DEMO & Subs.'),
(4, N'whtAbsorbedBy', N'Vendors', 1, N'Vendors'),
(4, N'whtAbsorbedBy', N'No', 1, N'No'),
(4, N'whtAbsorbedBy', N'Not Specific', 1, N'Not Specific')

SET IDENTITY_INSERT [dbo].[TenantSectionDetailOption] OFF
