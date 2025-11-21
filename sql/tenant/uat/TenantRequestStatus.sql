





DELETE FROM [dbo].[TenantRequestStatus]

--------------------------------------------------


INSERT [dbo].[TenantRequestStatus] ([TenantId], [RequestStatusId], [Name], [Description], [CanEdit], [CanDelete], [CanDuplicate], [CanCancel], [CanCopyToPR], [CanViewHistory], [CreatedBy], [CreatedDate], [UpdatedBy], [UpdatedDate]) VALUES

(1, 1, N'Draft', N'Draft', 1, 1, 1, 0, 0, 0, N'System', GETDATE(), NULL, NULL),
(1, 2, N'Awaiting', N'Awaiting', 0, 0, 1, 1, 0, 1, N'System', GETDATE(), NULL, NULL),
(1, 3, N'Partial Completed', N'Partial Completed', 0, 0, 1, 0, 1, 1, N'System', GETDATE(), NULL, NULL),
(1, 4, N'Completed', N'Completed', 0, 0, 1, 0, 1, 1, N'System', GETDATE(), NULL, NULL),
(1, 5, N'Rejected', N'Rejected', 0, 0, 0, 0, 0, 1, N'System', GETDATE(), NULL, NULL),
(1, 6, N'Cancelled', N'Cancelled', 0, 0, 0, 0, 0, 1, N'System', GETDATE(), NULL, NULL),
(1, 7, N'Pending', N'Awaiting', 0, 0, 1, 1, 0, 1, N'System', GETDATE(), NULL, NULL),

(2, 1, N'Draft', N'Draft', 1, 1, 1, 0, 0, 0, N'System', GETDATE(), NULL, NULL),
(2, 2, N'Awaiting', N'Awaiting', 0, 0, 1, 1, 0, 1, N'System', GETDATE(), NULL, NULL),
(2, 3, N'Partial Completed', N'Partial Completed', 0, 0, 1, 0, 0, 1, N'System', GETDATE(), NULL, NULL),
(2, 4, N'Completed', N'Completed', 0, 0, 1, 0, 0, 1, N'System', GETDATE(), NULL, NULL),
(2, 5, N'Rejected', N'Rejected', 0, 0, 1, 0, 0, 1, N'System', GETDATE(), NULL, NULL),
(2, 6, N'Cancelled', N'Cancelled', 0, 0, 0, 0, 0, 1, N'System', GETDATE(), NULL, NULL),
(2, 7, N'Pending', N'Awaiting', 0, 0, 1, 1, 0, 1, N'System', GETDATE(), NULL, NULL),

(3, 1, N'Draft', N'Draft', 1, 1, 1, 0, 0, 0, N'System', GETDATE(), NULL, NULL),
(3, 2, N'Awaiting', N'Awaiting', 0, 0, 1, 1, 0, 1, N'System', GETDATE(), NULL, NULL),
(3, 3, N'Partial Completed', N'Partial Completed', 0, 0, 1, 0, 0, 1, N'System', GETDATE(), NULL, NULL),
(3, 4, N'Completed', N'Completed', 0, 0, 1, 0, 0, 1, N'System', GETDATE(), NULL, NULL),
(3, 5, N'Rejected', N'Rejected', 0, 0, 1, 0, 0, 1, N'System', GETDATE(), NULL, NULL),
(3, 6, N'Cancelled', N'Cancelled', 0, 0, 0, 0, 0, 1, N'System', GETDATE(), NULL, NULL),
(3, 7, N'Pending', N'Awaiting', 0, 0, 1, 1, 0, 1, N'System', GETDATE(), NULL, NULL),

(4, 1, N'Draft', N'Draft', 1, 1, 1, 0, 0, 0, N'System', GETDATE(), NULL, NULL),
(4, 2, N'Awaiting', N'Awaiting', 0, 0, 1, 1, 0, 1, N'System', GETDATE(), NULL, NULL),
(4, 3, N'Partial Completed', N'Partial Completed', 0, 0, 1, 0, 0, 1, N'System', GETDATE(), NULL, NULL),
(4, 4, N'Completed', N'Completed', 0, 0, 1, 0, 0, 1, N'System', GETDATE(), NULL, NULL),
(4, 5, N'Rejected', N'Rejected', 0, 0, 1, 0, 0, 1, N'System', GETDATE(), NULL, NULL),
(4, 6, N'Cancelled', N'Cancelled', 0, 0, 0, 0, 0, 1, N'System', GETDATE(), NULL, NULL),
(4, 7, N'Pending', N'Awaiting', 0, 0, 1, 1, 0, 1, N'System', GETDATE(), NULL, NULL)