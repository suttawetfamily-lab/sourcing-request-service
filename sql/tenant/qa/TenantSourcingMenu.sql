


DELETE FROM [dbo].[TenantSourcingMenu]

--------------------------------------------------

INSERT [dbo].[TenantSourcingMenu] ([TenantId], [SourcingMenuId], [Visibled], [CreatedBy], [CreatedDate], [UpdatedBy], [UpdatedDate]) VALUES

(1, 1, 1, N'System', GETDATE(), NULL, NULL),

(1, 2, 1, N'System', GETDATE(), NULL, NULL),
(2, 1, 1, N'System', GETDATE(), NULL, NULL),

(3, 1, 1, N'System', GETDATE(), NULL, NULL),
(3, 2, 1, N'System', GETDATE(), NULL, NULL),

(4, 1, 1, N'System', GETDATE(), NULL, NULL),
(4, 2, 1, N'System', GETDATE(), NULL, NULL)