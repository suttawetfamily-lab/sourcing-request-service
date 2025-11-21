

DELETE FROM [dbo].[MasterGridField]


DBCC CHECKIDENT ('[MasterGridField]', RESEED, 1);



INSERT [MasterGridField] ([Code], [DisplayName], [GroupName], [Sequence], [Searchable], [Sorting], [Width], [Type], [visible], [CreatedBy], [CreatedDate], [UpdatedBy], [UpdatedDate])
 VALUES 
 (N'code', N'Category Code', N'CATEGORY', 1, 0, N'0', NULL, N'string', 1, N'System', GETDATE(), NULL, NULL),
 (N'name', N'Category Name', N'CATEGORY', 2, 1, N'1', NULL, N'string', 1, N'System', GETDATE(), NULL, NULL),
 (N'sequence', N'Sequence', N'CATEGORY', 3, 0, N'0', NULL, N'string', 1, N'System', GETDATE(), NULL, NULL),
 (N'isDefault', N'Default', N'CATEGORY', 4, 0, N'0', NULL, N'string', 1, N'System', GETDATE(), NULL, NULL),
 (N'active', N'Active', N'CATEGORY', 5, 0, N'0', NULL, N'string', 1, N'System', GETDATE(), NULL, NULL),
 (N'createdBy', N'Created By', N'CATEGORY', 6, 0, N'0', NULL, N'string', 1, N'System', GETDATE(), NULL, NULL),
 (N'updatedDate', N'Las Modified', N'CATEGORY', 7, 0, N'0', NULL, N'string', 1, N'System', GETDATE(), NULL, NULL),
 (N'createdByName', N'Created By', N'CATEGORY', 8, 0, N'0', NULL, N'string', 1, N'System', GETDATE(), NULL, NULL),

 (N'code', N'Category Code', N'SUB-CATEGORY', 1, 0, N'0', NULL, N'string', 1, N'System', GETDATE(), NULL, NULL),
 (N'name', N'Category Name', N'SUB-CATEGORY', 2, 1, N'1', NULL, N'string', 1, N'System', GETDATE(), NULL, NULL),
 (N'sequence', N'Sequence', N'SUB-CATEGORY', 3, 0, N'0', NULL, N'string', 1, N'System', GETDATE(), NULL, NULL),
 (N'isDefault', N'Default', N'SUB-CATEGORY', 4, 0, N'0', NULL, N'string', 1, N'System', GETDATE(), NULL, NULL),
 (N'active', N'Active', N'SUB-CATEGORY', 5, 0, N'0', NULL, N'string', 1, N'System', GETDATE(), NULL, NULL),
 (N'createdBy', N'Created By', N'SUB-CATEGORY', 6, 0, N'0', NULL, N'string', 1, N'System', GETDATE(), NULL, NULL),
 (N'updatedDate', N'Las Modified', N'SUB-CATEGORY', 7, 0, N'0', NULL, N'string', 1, N'System', GETDATE(), NULL, NULL),
 (N'createdByName', N'Created By', N'SUB-CATEGORY', 8, 0, N'0', NULL, N'string', 1, N'System', GETDATE(), NULL, NULL),


 (N'userId', N'User ID', N'APPROVER', 1, 0, N'0', NULL, N'string', 1, N'System', GETDATE(), NULL, NULL),
 (N'loginId', N'Login ID', N'APPROVER', 2, 0, N'0', NULL, N'string', 1, N'System', GETDATE(), NULL, NULL),
 (N'purchaserName', N'Purchaser Name', N'APPROVER', 3, 1, N'1', NULL, N'string', 1, N'System', GETDATE(), NULL, NULL),
 (N'email', N'Email', N'APPROVER', 4, 0, N'0', NULL, N'string', 1, N'System', GETDATE(), NULL, NULL),
 (N'phone', N'Phone', N'APPROVER', 5, 0, N'0', NULL, N'string', 1, N'System', GETDATE(), NULL, NULL),
 (N'sequence', N'Sequence', N'APPROVER', 6, 0, N'0', NULL, N'string', 1, N'System', GETDATE(), NULL, NULL),
 (N'isDefault', N'Default', N'APPROVER', 7, 0, N'0', NULL, N'string', 1, N'System', GETDATE(), NULL, NULL),
 (N'active', N'Active', N'APPROVER', 8, 0, N'0', NULL, N'string', 1, N'System', GETDATE(), NULL, NULL), 
 (N'createdBy', N'Created By', N'APPROVER', 9, 0, N'0', NULL, N'string', 1, N'System', GETDATE(), NULL, NULL),
 (N'updatedDate', N'Last Modified', N'APPROVER', 10, 0, N'0', NULL, N'string', 1, N'System', GETDATE(), NULL, NULL),
 (N'createdByName', N'Created By', N'APPROVER', 11, 0, N'0', NULL, N'string', 1, N'System', GETDATE(), NULL, NULL),
 
 (N'userId', N'User ID', N'REPORT-LINE', 1, 0, N'0', NULL, N'string', 1, N'System', GETDATE(), NULL, NULL),
 (N'loginId', N'Login ID', N'REPORT-LINE', 2, 0, N'0', NULL, N'string', 1, N'System', GETDATE(), NULL, NULL),
 (N'approverName', N'Approver Name', N'REPORT-LINE', 3, 1, N'1', NULL, N'string', 1, N'System', GETDATE(), NULL, NULL),
 (N'email', N'Email', N'REPORT-LINE', 4, 0, N'0', NULL, N'string', 1, N'System', GETDATE(), NULL, NULL),
 (N'phone', N'Phone', N'REPORT-LINE', 5, 0, N'0', NULL, N'string', 1, N'System', GETDATE(), NULL, NULL),
 (N'sequence', N'Sequence', N'REPORT-LINE', 6, 0, N'0', NULL, N'string', 1, N'System', GETDATE(), NULL, NULL),
 (N'isDefault', N'Default', N'REPORT-LINE', 7, 0, N'0', NULL, N'string', 1, N'System', GETDATE(), NULL, NULL),
 (N'active', N'Active', N'REPORT-LINE', 8, 0, N'0', NULL, N'string', 1, N'System', GETDATE(), NULL, NULL),
 (N'createdBy', N'Created By', N'REPORT-LINE', 9, 0, N'0', NULL, N'string', 1, N'System', GETDATE(), NULL, NULL),
 (N'updatedDate', N'Last Modified', N'REPORT-LINE', 10, 0, N'0', NULL, N'string', 1, N'System', GETDATE(), NULL, NULL),
 (N'createdByName', N'Created By', N'REPORT-LINE', 11, 0, N'0', NULL, N'string', 1, N'System', GETDATE(), NULL, NULL),
 
 (N'userId', N'User ID', N'PURCHASER', 1, 0, N'0', NULL, N'string', 1, N'System', GETDATE(), NULL, NULL),
 (N'loginId', N'Login ID', N'PURCHASER', 2, 0, N'0', NULL, N'string', 1, N'System', GETDATE(), NULL, NULL),
 (N'purchaserName', N'Purchaser Name', N'PURCHASER', 3, 1, N'1', NULL, N'string', 1, N'System', GETDATE(), NULL, NULL),
 (N'email', N'Email', N'PURCHASER', 4, 0, N'0', NULL, N'string', 1, N'System', GETDATE(), NULL, NULL),
 (N'phone', N'Phone', N'PURCHASER', 5, 0, N'0', NULL, N'string', 1, N'System', GETDATE(), NULL, NULL),
 (N'sequence', N'Sequence', N'PURCHASER', 6, 0, N'0', NULL, N'string', 1, N'System', GETDATE(), NULL, NULL),
 (N'isDefault', N'Default', N'PURCHASER', 7, 0, N'0', NULL, N'string', 1, N'System', GETDATE(), NULL, NULL),
 (N'active', N'Active', N'PURCHASER', 8, 0, N'0', NULL, N'string', 1, N'System', GETDATE(), NULL, NULL),
 (N'createdBy', N'Created By', N'PURCHASER', 9, 0, N'0', NULL, N'string', 1, N'System', GETDATE(), NULL, NULL),
 (N'updatedDate', N'Last Modified', N'PURCHASER', 10, 0, N'0', NULL, N'string', 1, N'System', GETDATE(), NULL, NULL),
 (N'createdByName', N'Created By', N'PURCHASER', 11, 0, N'0', NULL, N'string', 1, N'System', GETDATE(), NULL, NULL),
 
 (N'code', N'Department Code', N'DEPARTMENT', 1, 0, N'0', NULL, N'string', 1, N'System', GETDATE(), NULL, NULL),
 (N'name', N'Department Name', N'DEPARTMENT', 2, 1, N'1', NULL, N'string', 1, N'System', GETDATE(), NULL, NULL),
 (N'sequence', N'Sequence', N'DEPARTMENT', 3, 0, N'0', NULL, N'string', 1, N'System', GETDATE(), NULL, NULL),
 (N'isDefault', N'Default', N'DEPARTMENT', 4, 0, N'0', NULL, N'string', 1, N'System', GETDATE(), NULL, NULL),
 (N'active', N'Active', N'DEPARTMENT', 5, 0, N'0', NULL, N'string', 1, N'System', GETDATE(), NULL, NULL),
 (N'createdBy', N'Created By', N'DEPARTMENT', 6, 0, N'0', NULL, N'string', 1, N'System', GETDATE(), NULL, NULL),
 (N'updatedDate', N'Las Modified', N'DEPARTMENT', 7, 0, N'0', NULL, N'string', 1, N'System', GETDATE(), NULL, NULL),
 (N'createdByName', N'Created By', N'DEPARTMENT', 8, 0, N'0', NULL, N'string', 1, N'System', GETDATE(), NULL, NULL),

 (N'code', N'Project Code', N'PROJECT/CURRENT-DATA', 1, 0, N'0', NULL, N'string', 1, N'System', GETDATE(), NULL, NULL),
 (N'name', N'Project Name', N'PROJECT/CURRENT-DATA', 2, 1, N'1', NULL, N'string', 1, N'System', GETDATE(), NULL, NULL),
 (N'sequence', N'Sequence', N'PROJECT/CURRENT-DATA', 3, 0, N'0', NULL, N'string', 1, N'System', GETDATE(), NULL, NULL),
 (N'isDefault', N'Default', N'PROJECT/CURRENT-DATA', 4, 0, N'0', NULL, N'string', 1, N'System', GETDATE(), NULL, NULL),
 (N'active', N'Active', N'PROJECT/CURRENT-DATA', 5, 0, N'0', NULL, N'string', 1, N'System', GETDATE(), NULL, NULL),
 (N'createdBy', N'Created By', N'PROJECT/CURRENT-DATA', 6, 0, N'0', NULL, N'string', 1, N'System', GETDATE(), NULL, NULL),
 (N'updatedDate', N'Las Modified', N'PROJECT/CURRENT-DATA', 7, 0, N'0', NULL, N'string', 1, N'System', GETDATE(), NULL, NULL),
 (N'createdByName', N'Created By', N'PROJECT/CURRENT-DATA', 8, 0, N'0', NULL, N'string', 1, N'System', GETDATE(), NULL, NULL),

 (N'fileId', N'File ID', N'PROJECT/UPLOAD-STATUS', 1, 0, N'0', NULL, N'string', 1, N'System', GETDATE(), NULL, NULL),
 (N'fileName', N'File Name', N'PROJECT/UPLOAD-STATUS', 2, 1, N'1', NULL, N'string', 1, N'System', GETDATE(), NULL, NULL),
 (N'fileSize', N'File Size', N'PROJECT/UPLOAD-STATUS', 3, 0, N'0', NULL, N'string', 1, N'System', GETDATE(), NULL, NULL),
 (N'createdBy', N'Created By', N'PROJECT/UPLOAD-STATUS', 4, 0, N'0', NULL, N'string', 1, N'System', GETDATE(), NULL, NULL),
 (N'updatedDate', N'Las Modified', N'PROJECT/UPLOAD-STATUS', 5, 0, N'0', NULL, N'string', 1, N'System', GETDATE(), NULL, NULL),
 (N'createdByName', N'Created By', N'PROJECT/UPLOAD-STATUS', 6, 0, N'0', NULL, N'string', 1, N'System', GETDATE(), NULL, NULL),

 (N'userId', N'User ID', N'REVIEWER', 1, 0, N'0', NULL, N'string', 1, N'System', GETDATE(), NULL, NULL),
 (N'loginId', N'Login ID', N'REVIEWER', 2, 0, N'0', NULL, N'string', 1, N'System', GETDATE(), NULL, NULL),
 (N'reviewerName', N'Reviewer Name', N'REVIEWER', 3, 1, N'1', NULL, N'string', 1, N'System', GETDATE(), NULL, NULL),
 (N'email', N'Email', N'REVIEWER', 4, 0, N'0', NULL, N'string', 1, N'System', GETDATE(), NULL, NULL),
 (N'phone', N'Phone', N'REVIEWER', 5, 0, N'0', NULL, N'string', 1, N'System', GETDATE(), NULL, NULL),
 (N'sequence', N'Sequence', N'REVIEWER', 6, 0, N'0', NULL, N'string', 1, N'System', GETDATE(), NULL, NULL),
 (N'isDefault', N'Default', N'REVIEWER', 7, 0, N'0', NULL, N'string', 1, N'System', GETDATE(), NULL, NULL),
 (N'active', N'Active', N'REVIEWER', 8, 0, N'0', NULL, N'string', 1, N'System', GETDATE(), NULL, NULL),
 (N'createdBy', N'Created By', N'REVIEWER', 9, 0, N'0', NULL, N'string', 1, N'System', GETDATE(), NULL, NULL),
 (N'updatedDate', N'Last Modified', N'REVIEWER', 10, 0, N'0', NULL, N'string', 1, N'System', GETDATE(), NULL, NULL),
 (N'createdByName', N'Created By', N'REVIEWER', 11, 0, N'0', NULL, N'string', 1, N'System', GETDATE(), NULL, NULL)






