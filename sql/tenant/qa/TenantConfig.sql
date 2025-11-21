

DELETE FROM [dbo].[TenantConfig]


DBCC CHECKIDENT ('[TenantConfig]', RESEED, 1);
--------------------------------------------------

INSERT [dbo].[TenantConfig] ([TenantId], [Topic], [Section], [Name], [Value], [Sequence], [CreatedBy], [CreatedDate], [UpdatedBy], [UpdatedDate]) VALUES


(1, N'API', N'CreateToPr', N'URL', N'CreateDraftPCP', 1, N'System', GETDATE(), NULL, NULL),
(1, N'API', N'ProjectList', N'URL', N'GetProjectActiveAllList', 1, N'System', GETDATE(), NULL, NULL),

(1, N'Configuration', N'Approval', N'isShowAllApprovalTap', N'true', 1, N'System', GETDATE(), NULL, NULL),
(1, N'Configuration', N'DeptApprover', N'Enable', N'false', 1, N'System', GETDATE(), NULL, NULL),
(1, N'Configuration', N'DeptApprover', N'sectionLabel', N'Approver', 1, N'System', GETDATE(), NULL, NULL),

(1, N'Configuration', N'EPAuth', N'CanViewBorgAll', N'false', 1, N'System', GETDATE(), NULL, NULL),
(1, N'Configuration', N'Excel', N'CanShowCellProject', N'true', 1, N'System', GETDATE(), NULL, NULL),
(1, N'Configuration', N'Excel', N'maximumUploadItem', N'200', 1, N'System', GETDATE(), NULL, NULL),
(1, N'Configuration', N'ExistingPriceItem', N'isShowExistingPriceCreateSourcing', N'true', 1, N'System', GETDATE(), NULL, NULL),
-- (1, N'Configuration', N'ExistingPriceItem', N'Menu', N'true', 1, N'System', GETDATE(), NULL, NULL),
(1, N'Configuration', N'ExportReportDateRange', N'ExportReportTitleSelectDate', N'Please select a date range within 365 days.', 1, N'System', GETDATE(), NULL, NULL),
(1, N'Configuration', N'ExportReportDateRange', N'InvalidErrorMessage', N'The date range is greater than 365 days.', 1, N'System', GETDATE(), NULL, NULL),
(1, N'Configuration', N'ExportReportDateRange', N'Locked', N'true', 1, N'System', GETDATE(), NULL, NULL),
(1, N'Configuration', N'ExportReportDateRange', N'OffsetCurrentYear', N'false', 1, N'System', GETDATE(), NULL, NULL),
(1, N'Configuration', N'ExportReportDateRange', N'Year', N'1', 1, N'System', GETDATE(), NULL, NULL),

(1, N'Configuration', N'HistoryModal', N'ProjectNameLabel', N'Project / Department', 1, N'System', GETDATE(), NULL, NULL),
(1, N'Configuration', N'HistoryModal', N'RequestNameLabel', N'Request Name', 1, N'System', GETDATE(), NULL, NULL),
(1, N'Configuration', N'HistoryModal', N'ProjectNameFormat', N'', 1, N'System', GETDATE(), NULL, NULL),
(1, N'Configuration', N'LogicStep1', N'checkWaringChangeFields', N'requestTypeId,types', 1, N'System', GETDATE(), NULL, NULL),
(1, N'Configuration', N'LogicStep2', N'defaultPhoneFieldName', N'contactPhone', 1, N'System', GETDATE(), NULL, NULL),
(1, N'Configuration', N'Purchaser', N'sectionLabel', N'Purchaser', 1, N'System', GETDATE(), NULL, NULL),

(1, N'Configuration', N'Report', N'Filtering', N'false', 1, N'System', GETDATE(), NULL, NULL),
(1, N'Configuration', N'RequestItem', N'reasonModalTitle', N'Reason', 1, N'System', GETDATE(), NULL, NULL),
(1, N'Configuration', N'RequestSubmit', N'isAutoAssignToPurchaser', N'false', 1, N'System', GETDATE(), NULL, NULL),
(1, N'Configuration', N'RequestView', N'requestNoteLabel', N'Note to Purchaser', 1, N'System', GETDATE(), NULL, NULL),

(1, N'Configuration', N'Show/Hide', N'ForwardApprovalWorkflow', N'false', 1, N'System', GETDATE(), NULL, NULL),
(1, N'Configuration', N'Show/Hide', N'isAllowDeleteItemForApprover', N'true', 1, N'System', GETDATE(), NULL, NULL),
(1, N'Configuration', N'Show/Hide', N'isShowApprovalApproverGroup', N'false', 1, N'System', GETDATE(), NULL, NULL),
(1, N'Configuration', N'Show/Hide', N'isShowApprovalReportLine', N'false', 1, N'System', GETDATE(), NULL, NULL),
(1, N'Configuration', N'Show/Hide', N'isShowMenuAssignToMe', N'true', 1, N'System', GETDATE(), NULL, NULL),
(1, N'Configuration', N'Show/Hide', N'isShowMenuEditRequestForApprover', N'false', 1, N'System', GETDATE(), NULL, NULL),
(1, N'Configuration', N'Show/Hide', N'isShowRemoveFromMyTaskActionMenu', N'true', 1, N'System', GETDATE(), NULL, NULL),
(1, N'Configuration', N'Show/Hide', N'isShowServices', N'false', 1, N'System', GETDATE(), NULL, NULL),
(1, N'Configuration', N'Show/Hide', N'isShowStep3', N'false', 1, N'System', GETDATE(), NULL, NULL),
(1, N'Configuration', N'Show/Hide', N'isShowViewRequestDetail', N'false', 1, N'System', GETDATE(), NULL, NULL),

(1, N'Configuration', N'Template', N'approvalSectionTemplate', N'template00', 1, N'System', GETDATE(), NULL, NULL),
(1, N'Configuration', N'Template', N'viewApproveRequestItemTableTemplate', N'standard', 1, N'System', GETDATE(), NULL, NULL),
(1, N'Configuration', N'WorkflowEngine', N'Enable', N'true', 1, N'System', GETDATE(), NULL, NULL),

(1, N'Email', N'EmailAddress', N'Noreply', N'noreply@pantavanij.com', 1, N'System', GETDATE(), NULL, NULL),
(1, N'Email', N'Image', N'EBO_LO', N'EBO_AIT_LO', 1, N'System', GETDATE(), NULL, NULL),
(1, N'Email', N'Image', N'EBO_PHONE', N'EBO_PHONE', 1, N'System', GETDATE(), NULL, NULL),
(1, N'Email', N'Image', N'EMAIL_IMAGE_URL', N'https://ep.pantavanij.com/images/lo/%s.png', 1, N'System', GETDATE(), NULL, NULL),
(1, N'Email', N'LoginUrl', N'LOGIN_EP_URL', N'https://ep-stg.pantavanij.com/Firstpage.action', 1, N'System', GETDATE(), NULL, NULL),
(1, N'Email', N'TemplateId', N'PLEASE_REVIEW_AND_APPROVE_SR', N'175', 1, N'System', GETDATE(), NULL, NULL),
(1, N'Email', N'TemplateId', N'PLEASE_REVIEW_SR', N'176', 1, N'System', GETDATE(), NULL, NULL),
(1, N'Email', N'TemplateId', N'SR_HAS_BEEN_APPROVED', N'178', 1, N'System', GETDATE(), NULL, NULL),
(1, N'Email', N'TemplateId', N'SR_HAS_BEEN_CANCELLED', N'179', 1, N'System', GETDATE(), NULL, NULL),
(1, N'Email', N'TemplateId', N'SR_HAS_BEEN_REJECTED', N'177', 1, N'System', GETDATE(), NULL, NULL),

--(1, N'ERFX', N'Auth_Code', N'GetStatus', N'J3H52WLVWCR1', 1, N'System', GETDATE(), NULL, NULL),
--(1, N'ERFX', N'eRFXNo', N'MaxItems', N'50', 1, N'System', GETDATE(), NULL, NULL),

(1, N'Logo', N'Image', N'LOGO_IMAGE_URL', N'https://ep.pantavanij.com/images/logo/ait.png', 1, N'System', GETDATE(), NULL, NULL),

(1, N'Supplier Web Work', N'Search Supplier', N'Limit', N'50', 1, N'System', GETDATE(), NULL, NULL),
(1, N'Timeout', N'Minutes', N'Session', N'30', 1, N'System', GETDATE(), NULL, NULL),
(1, N'Template', N'RequestItem', N'NA', N'Template_Upload_Item.xlsx', 1, N'System', GETDATE(), NULL, NULL),

(1, N'UserInfo', N'Approver', N'PrivilegeCode', N'sqa', 1, N'System', GETDATE(), NULL, NULL),
(1, N'UserInfo', N'ReportLine', N'PrivilegeCode', N'srl', 1, N'System', GETDATE(), NULL, NULL),
(1, N'UserInfo', N'Requester', N'PrivilegeCode', N'sqr', 1, N'System', GETDATE(), NULL, NULL),
(1, N'UserInfo', N'Reviewer', N'PrivilegeCode', N'sqv', 1, N'System', GETDATE(), NULL, NULL),
(1, N'UserInfo', N'UAAParam', N'IDP', N'EP', 1, N'System', GETDATE(), NULL, NULL),

(1, N'Workflow', N'TemplateId', N'NA', N'60565', 1, N'System', GETDATE(), NULL, NULL),
(1, N'Workflow', N'ApprovalType', N'NA', N'Purchaser', 1, N'System', GETDATE(), NULL, NULL),
(1, N'Workflow', N'TemplateId-02', N'NA', N'47236', 1, N'System', GETDATE(), NULL, NULL),


----------------------------------------------------------------------------------------------------------


(2, N'API', N'CreateToPr', N'URL', N'CreateDraftPCP', 1, N'System', GETDATE(), NULL, NULL),
(2, N'API', N'ProjectList', N'URL', N'GetProjectActiveAllList', 1, N'System', GETDATE(), NULL, NULL),

(2, N'Configuration', N'Approval', N'isShowAllApprovalTap', N'false', 1, N'System', GETDATE(), NULL, NULL),
(2, N'Configuration', N'DeptApprover', N'Enable', N'false', 1, N'System', GETDATE(), NULL, NULL),
(2, N'Configuration', N'DeptApprover', N'sectionLabel', N'Approver', 1, N'System', GETDATE(), NULL, NULL),

(2, N'Configuration', N'EPAuth', N'CanViewBorgAll', N'true', 1, N'System', GETDATE(), NULL, NULL),
(2, N'Configuration', N'EPAuth', N'SessionTimeout', N'false', 1, N'System', GETDATE(), NULL, NULL),
(2, N'Configuration', N'Excel', N'CanShowCellProject', N'true', 1, N'System', GETDATE(), NULL, NULL),
(2, N'Configuration', N'Excel', N'maximumUploadItem', N'500', 1, N'System', GETDATE(), NULL, NULL),
(2, N'Configuration', N'ExistingPriceItem', N'isShowExistingPriceCreateSourcing', N'true', 1, N'System', GETDATE(), NULL, NULL),
(2, N'Configuration', N'ExistingPriceItem', N'Menu', N'true', 1, N'System', GETDATE(), NULL, NULL),
(2, N'Configuration', N'ExportReportDateRange', N'ExportReportTitleSelectDate', N'Please select a date range within 365 days.', 1, N'System', GETDATE(), NULL, NULL),
(2, N'Configuration', N'ExportReportDateRange', N'InvalidErrorMessage', N'The date range is greater than 365 days.', 1, N'System', GETDATE(), NULL, NULL),
(2, N'Configuration', N'ExportReportDateRange', N'Locked', N'true', 1, N'System', GETDATE(), NULL, NULL),
(2, N'Configuration', N'ExportReportDateRange', N'OffsetCurrentYear', N'false', 1, N'System', GETDATE(), NULL, NULL),
(2, N'Configuration', N'ExportReportDateRange', N'Year', N'1', 1, N'System', GETDATE(), NULL, NULL),

(2, N'Configuration', N'HistoryModal', N'ProjectNameFormat', N'projectName (projectCode)', 1, N'System', GETDATE(), NULL, NULL),
(2, N'Configuration', N'HistoryModal', N'ProjectNameLabel', N'Project Name (Project Code)', 1, N'System', GETDATE(), NULL, NULL),
(2, N'Configuration', N'HistoryModal', N'RequestNameLabel', N'Sourcing Request Name', 1, N'System', GETDATE(), NULL, NULL),
(2, N'Configuration', N'LogicStep1', N'checkWaringChangeFields', N'requestTypeId,types', 1, N'System', GETDATE(), NULL, NULL),
(2, N'Configuration', N'LogicStep2', N'defaultPhoneFieldName', N'phone', 1, N'System', GETDATE(), NULL, NULL),

(2, N'Configuration', N'RequestSubmit', N'isAutoAssignToPurchaser', N'true', 1, N'System', GETDATE(), NULL, NULL),
(2, N'Configuration', N'RequestView', N'requestNoteLabel', N'Note', 1, N'System', GETDATE(), NULL, NULL),
(2, N'Configuration', N'RequestItem', N'reasonModalTitle', N'Reason for Item Rejection', 1, N'System', GETDATE(), NULL, NULL),
(2, N'Configuration', N'Purchaser', N'sectionLabel', N'Purchaser', 1, N'System', GETDATE(), NULL, NULL),
(2, N'Configuration', N'Report', N'Filtering', N'true', 1, N'System', GETDATE(), NULL, NULL),

(2, N'Configuration', N'Show/Hide', N'ForwardApprovalWorkflow', N'true', 1, N'System', GETDATE(), NULL, NULL),
(2, N'Configuration', N'Show/Hide', N'isAllowDeleteItemForApprover', N'false', 1, N'System', GETDATE(), NULL, NULL),
(2, N'Configuration', N'Show/Hide', N'isShowApprovalApproverGroup', N'false', 1, N'System', GETDATE(), NULL, NULL),
(2, N'Configuration', N'Show/Hide', N'isShowApprovalReportLine', N'true', 1, N'System', GETDATE(), NULL, NULL),
(2, N'Configuration', N'Show/Hide', N'isShowMenuAssignToMe', N'false', 1, N'System', GETDATE(), NULL, NULL),
(2, N'Configuration', N'Show/Hide', N'isShowMenuEditRequestForApprover', N'true', 1, N'System', GETDATE(), NULL, NULL),
(2, N'Configuration', N'Show/Hide', N'isShowRemoveFromMyTaskActionMenu', N'false', 1, N'System', GETDATE(), NULL, NULL),
(2, N'Configuration', N'Show/Hide', N'isShowServices', N'true', 1, N'System', GETDATE(), NULL, NULL),
(2, N'Configuration', N'Show/Hide', N'isShowStep3', N'true', 1, N'System', GETDATE(), NULL, NULL),
(2, N'Configuration', N'Show/Hide', N'isShowViewRequestDetail', N'true', 1, N'System', GETDATE(), NULL, NULL),

(2, N'Configuration', N'Template', N'approvalSectionTemplate', N'template01', 1, N'System', GETDATE(), NULL, NULL),
(2, N'Configuration', N'Template', N'viewApproveRequestItemTableTemplate', N'template01', 1, N'System', GETDATE(), NULL, NULL),
(2, N'Configuration', N'WorkflowEngine', N'Enable', N'true', 1, N'System', GETDATE(), NULL, NULL),

(2, N'Email', N'TemplateId', N'PLEASE_REVIEW_AND_APPROVE_SR', N'264', 1, N'System', GETDATE(), NULL, NULL),
(2, N'Email', N'TemplateId', N'PLEASE_REVIEW_SR', N'200', 1, N'System', GETDATE(), NULL, NULL),
(2, N'Email', N'TemplateId', N'SR_HAS_BEEN_REJECTED', N'267', 1, N'System', GETDATE(), NULL, NULL),
(2, N'Email', N'TemplateId', N'SR_HAS_BEEN_CANCELLED', N'270', 1, N'System', GETDATE(), NULL, NULL),
(2, N'Email', N'TemplateId', N'SR_HAS_BEEN_APPROVED', N'266', 1, N'System', GETDATE(), NULL, NULL),
(2, N'Email', N'TemplateId', N'SR_HAS_BEEN_FORWARDED_FOR_PURCHASER_WHO_FORWARD_REQUEST_REQUESTER', N'204', 1, N'System', GETDATE(), NULL, NULL),
(2, N'Email', N'TemplateId', N'SR_HAS_BEEN_FORWARDED_FOR_PURCHASER_WHO_RECEIVED_FORWARDED_REQUEST', N'205', 1, N'System', GETDATE(), NULL, NULL),
(2, N'Email', N'TemplateId', N'DELEGATION_HAS_BEEN_CREATED_FOR_WHO_DELEGATE', N'206', 1, N'System', GETDATE(), NULL, NULL),
(2, N'Email', N'TemplateId', N'DELEGATION_HAS_BEEN_CREATED_FOR_WHO_RECEIVED_DELEGATE', N'207', 1, N'System', GETDATE(), NULL, NULL),
(2, N'Email', N'TemplateId', N'DELEGATION_HAS_BEEN_CANCELLED', N'208', 1, N'System', GETDATE(), NULL, NULL),

(2, N'Email', N'EmailAddress', N'Noreply', N'noreply@pantavanij.com', 1, N'System', GETDATE(), NULL, NULL),
(2, N'Email', N'Image', N'EBO_LO', N'sr_email', 1, N'System', GETDATE(), NULL, NULL),
(2, N'Email', N'Image', N'EBO_PHONE', N'sr_phone', 1, N'System', GETDATE(), NULL, NULL),
(2, N'Email', N'Image', N'EMAIL_IMAGE_URL', N'https://sourcingreq-qa.pantavanij.com/%s.png', 1, N'System', GETDATE(), NULL, NULL),
(2, N'Email', N'LoginUrl', N'LOGIN_EP_URL', N'https://bayep-qa.pantavanij.com/Firstpage.action', 1, N'System', GETDATE(), NULL, NULL),

(2, N'Logo', N'Image', N'LOGO_IMAGE_URL', N'https://ep.pantavanij.com/images/logo/bay.png', 1, N'System', GETDATE(), NULL, NULL),

(2, N'Supplier Web Work', N'Search Supplier', N'Limit', N'50', 1, N'System', GETDATE(), NULL, NULL),
(2, N'Template', N'RequestItem', N'NA', N'Template_Upload_Item.xlsx', 1, N'System', GETDATE(), NULL, NULL),
(2, N'Timeout', N'Minutes', N'Session', N'30', 1, N'System', GETDATE(), NULL, NULL),

(2, N'OurService', N'SourcingRequest', N'Account Setting', N'https://qa-delegate.oneplanetapp.com', 3, N'System', GETDATE(), NULL, NULL),
(2, N'OurService', N'SourcingRequest', N'Administration', N'https://alpha-admin.pantavanij.com', 1, N'System', GETDATE(), NULL, NULL),
(2, N'OurService', N'SourcingRequest', N'Form', N'https://eform-qa.pantavanij.com', 2, N'System', GETDATE(), NULL, NULL),
(2, N'OurService', N'SourcingRequest', N'Sourcing Request', N'https://sourcingreq-qa.pantavanij.com/sourcing-request', 4, N'System', GETDATE(), NULL, NULL),

(2, N'UserInfo', N'Approver', N'PrivilegeCode', N'sqa', 1, N'System', GETDATE(), NULL, NULL),
(2, N'UserInfo', N'ReportLine', N'PrivilegeCode', N'srl', 1, N'System', GETDATE(), NULL, NULL),
(2, N'UserInfo', N'Requester', N'PrivilegeCode', N'sqr', 1, N'System', GETDATE(), NULL, NULL),
(2, N'UserInfo', N'Reviewer', N'PrivilegeCode', N'sqv', 1, N'System', GETDATE(), NULL, NULL),
(2, N'UserInfo', N'UAAParam', N'IDP', N'EP', 1, N'System', GETDATE(), NULL, NULL),

(2, N'Workflow', N'TemplateId', N'NA', N'107937', 1, N'System', GETDATE(), NULL, NULL),
(2, N'Workflow', N'ApprovalType', N'NA', N'Purchaser', 1, N'System', GETDATE(), NULL, NULL),
(2, N'Workflow', N'TemplateId', N'eRFX', N'107948', 0, N'System', GETDATE(), NULL, NULL),


----------------------------------------------------------------------------------------------------------


(3, N'API', N'CreateToPr', N'URL', N'CreateDraftPCP', 1, N'System', GETDATE(), NULL, NULL),
(3, N'API', N'ProjectList', N'URL', N'GetProjectActiveAllList', 1, N'System', GETDATE(), NULL, NULL),

(3, N'Configuration', N'Approval', N'isShowAllApprovalTap', N'false', 1, N'System', GETDATE(), NULL, NULL),
(3, N'Configuration', N'DeptApprover', N'Enable', N'true', 1, N'System', GETDATE(), NULL, NULL),
(3, N'Configuration', N'DeptApprover', N'sectionLabel', N'Approver', 1, N'System', GETDATE(), NULL, NULL),

(3, N'Configuration', N'EPAuth', N'CanViewBorgAll', N'true', 1, N'System', GETDATE(), NULL, NULL),
(3, N'Configuration', N'EPAuth', N'SessionTimeout', N'false', 1, N'System', GETDATE(), NULL, NULL),
(3, N'Configuration', N'Excel', N'CanShowCellProject', N'true', 1, N'System', GETDATE(), NULL, NULL),
(3, N'Configuration', N'Excel', N'maximumUploadItem', N'500', 1, N'System', GETDATE(), NULL, NULL),
(3, N'Configuration', N'ExistingPriceItem', N'Menu', N'true', 1, N'System', GETDATE(), NULL, NULL),
(3, N'Configuration', N'ExistingPriceItem', N'isShowExistingPriceCreateSourcing', N'true', 1, N'System', GETDATE(), NULL, NULL),

(3, N'Configuration', N'ExportReportDateRange', N'ExportReportTitleSelectDate', N'Please select dates from within the last 3 years and this year.', 1, N'System', GETDATE(), NULL, NULL),
(3, N'Configuration', N'ExportReportDateRange', N'InvalidErrorMessage', N'The date range is greater than the last 3 years and this year.', 1, N'System', GETDATE(), NULL, NULL),
(3, N'Configuration', N'ExportReportDateRange', N'Locked', N'false', 1, N'System', GETDATE(), NULL, NULL),
(3, N'Configuration', N'ExportReportDateRange', N'OffsetCurrentYear', N'true', 1, N'System', GETDATE(), NULL, NULL),
(3, N'Configuration', N'ExportReportDateRange', N'Year', N'3', 1, N'System', GETDATE(), NULL, NULL),

(3, N'Configuration', N'HistoryModal', N'ProjectNameFormat', N'', 1, N'System', GETDATE(), NULL, NULL),
(3, N'Configuration', N'HistoryModal', N'ProjectNameLabel', N'Project', 1, N'System', GETDATE(), NULL, NULL),
(3, N'Configuration', N'HistoryModal', N'RequestNameLabel', N'Sourcing Request Name', 1, N'System', GETDATE(), NULL, NULL),
(3, N'Configuration', N'LogicStep1', N'checkWaringChangeFields', N'requestTypeId', 1, N'System', GETDATE(), NULL, NULL),
(3, N'Configuration', N'LogicStep2', N'defaultPhoneFieldName', N'phone', 1, N'System', GETDATE(), NULL, NULL),

(3, N'Configuration', N'Purchaser', N'sectionLabel', N'Purchaser', 1, N'System', GETDATE(), NULL, NULL),
(3, N'Configuration', N'Report', N'Filtering', N'true', 1, N'System', GETDATE(), NULL, NULL),
(3, N'Configuration', N'RequestItem', N'reasonModalTitle', N'Reason for Item Rejection', 1, N'System', GETDATE(), NULL, NULL),
(3, N'Configuration', N'RequestSubmit', N'isAutoAssignToPurchaser', N'true', 1, N'System', GETDATE(), NULL, NULL),
(3, N'Configuration', N'RequestView', N'requestNoteLabel', N'Note', 1, N'System', GETDATE(), NULL, NULL),

(3, N'Configuration', N'Show/Hide', N'ForwardApprovalWorkflow', N'true', 1, N'System', GETDATE(), NULL, NULL),
(3, N'Configuration', N'Show/Hide', N'isAllowDeleteItemForApprover', N'true', 1, N'System', GETDATE(), NULL, NULL),
(3, N'Configuration', N'Show/Hide', N'isShowApprovalReportLine', N'false', 1, N'System', GETDATE(), NULL, NULL),
(3, N'Configuration', N'Show/Hide', N'isShowApprovalApproverGroup', N'true', 1, N'System', GETDATE(), NULL, NULL),
(3, N'Configuration', N'Show/Hide', N'isShowMenuAssignToMe', N'false', 1, N'System', GETDATE(), NULL, NULL),
(3, N'Configuration', N'Show/Hide', N'isShowMenuEditRequestForApprover', N'true', 1, N'System', GETDATE(), NULL, NULL),
(3, N'Configuration', N'Show/Hide', N'isShowRemoveFromMyTaskActionMenu', N'false', 1, N'System', GETDATE(), NULL, NULL),
(3, N'Configuration', N'Show/Hide', N'isShowServices', N'false', 1, N'System', GETDATE(), NULL, NULL),
(3, N'Configuration', N'Show/Hide', N'isShowStep3', N'false', 1, N'System', GETDATE(), NULL, NULL),
(3, N'Configuration', N'Show/Hide', N'isShowViewRequestDetail', N'true', 1, N'System', GETDATE(), NULL, NULL),

(3, N'Configuration', N'Template', N'approvalSectionTemplate', N'template02', 1, N'System', GETDATE(), NULL, NULL),
(3, N'Configuration', N'Template', N'viewApproveRequestItemTableTemplate', N'template02', 1, N'System', GETDATE(), NULL, NULL),
(3, N'Configuration', N'WorkflowEngine', N'Enable', N'false', 1, N'System', GETDATE(), NULL, NULL),

(3, N'Email', N'EmailAddress', N'Noreply', N'noreply@pantavanij.com', 1, N'System', GETDATE(), NULL, NULL),
(3, N'Email', N'Image', N'EBO_LO', N'sr_email', 1, N'System', GETDATE(), NULL, NULL),
(3, N'Email', N'Image', N'EBO_PHONE', N'sr_phone', 1, N'System', GETDATE(), NULL, NULL),
(3, N'Email', N'Image', N'EMAIL_IMAGE_URL', N'https://sourcingreq-qa.pantavanij.com/%s.png', 1, N'System', GETDATE(), NULL, NULL),
(3, N'Email', N'LoginUrl', N'LOGIN_EP_URL', N'https://bayep-qa.pantavanij.com/Firstpage.action', 1, N'System', GETDATE(), NULL, NULL),

(3, N'Email', N'TemplateId', N'PLEASE_REVIEW_AND_APPROVE_SR', N'264', 1, N'System', GETDATE(), NULL, NULL),
(3, N'Email', N'TemplateId', N'PLEASE_REVIEW_SR', N'200', 1, N'System', GETDATE(), NULL, NULL),
(3, N'Email', N'TemplateId', N'SR_HAS_BEEN_REJECTED', N'267', 1, N'System', GETDATE(), NULL, NULL),
(3, N'Email', N'TemplateId', N'SR_HAS_BEEN_CANCELLED', N'270', 1, N'System', GETDATE(), NULL, NULL),
(3, N'Email', N'TemplateId', N'SR_HAS_BEEN_APPROVED', N'266', 1, N'System', GETDATE(), NULL, NULL),
(3, N'Email', N'TemplateId', N'SR_HAS_BEEN_FORWARDED_FOR_PURCHASER_WHO_FORWARD_REQUEST_REQUESTER', N'204', 1, N'System', GETDATE(), NULL, NULL),
(3, N'Email', N'TemplateId', N'SR_HAS_BEEN_FORWARDED_FOR_PURCHASER_WHO_RECEIVED_FORWARDED_REQUEST', N'205', 1, N'System', GETDATE(), NULL, NULL),
(3, N'Email', N'TemplateId', N'DELEGATION_HAS_BEEN_CREATED_FOR_WHO_DELEGATE', N'206', 1, N'System', GETDATE(), NULL, NULL),
(3, N'Email', N'TemplateId', N'DELEGATION_HAS_BEEN_CREATED_FOR_WHO_RECEIVED_DELEGATE', N'207', 1, N'System', GETDATE(), NULL, NULL),
(3, N'Email', N'TemplateId', N'DELEGATION_HAS_BEEN_CANCELLED', N'208', 1, N'System', GETDATE(), NULL, NULL),

(3, N'Logo', N'Image', N'LOGO_IMAGE_URL', N'https://ep.pantavanij.com/images/logo/lotus.png', 1, N'System', GETDATE(), NULL, NULL),

(3, N'OurService', N'SourcingRequest', N'Account Setting', N'https://qa-delegate.oneplanetapp.com', 3, N'System', GETDATE(), NULL, NULL),
(3, N'OurService', N'SourcingRequest', N'Administration', N'https://alpha-admin.pantavanij.com', 1, N'System', GETDATE(), NULL, NULL),
(3, N'OurService', N'SourcingRequest', N'Form', N'https://eform-qa.pantavanij.com', 2, N'System', GETDATE(), NULL, NULL),
(3, N'OurService', N'SourcingRequest', N'Sourcing Request', N'https://sourcingreq-qa.pantavanij.com/sourcing-request', 4, N'System', GETDATE(), NULL, NULL),

--(3, N'Sourcing Status', N'Id', N'Deleted', N'10', 1, N'System', GETDATE(), NULL, NULL),

(3, N'Supplier Web Work', N'Search Supplier', N'Limit', N'50', 1, N'System', GETDATE(), NULL, NULL),
(3, N'Template', N'RequestItem', N'NA', N'Template_Upload_Item.xlsx', 1, N'System', GETDATE(), NULL, NULL),
(3, N'Timeout', N'Minutes', N'Session', N'30', 1, N'System', GETDATE(), NULL, NULL),

(3, N'UserInfo', N'Approver', N'PrivilegeCode', N'sqa', 1, N'System', GETDATE(), NULL, NULL),
(3, N'UserInfo', N'ReportLine', N'PrivilegeCode', N'srl', 1, N'System', GETDATE(), NULL, NULL),
(3, N'UserInfo', N'Requester', N'PrivilegeCode', N'sqn', 1, N'System', GETDATE(), NULL, NULL),
(3, N'UserInfo', N'Reviewer', N'PrivilegeCode', N'sqv', 1, N'System', GETDATE(), NULL, NULL),
(3, N'UserInfo', N'UAAParam', N'IDP', N'EP', 1, N'System', GETDATE(), NULL, NULL),

(3, N'Workflow', N'ApprovalType', N'NA', N'Purchaser', 1, N'System', GETDATE(), NULL, NULL),
(3, N'Workflow', N'TemplateId', N'eRFX', N'107974', 0, N'System', GETDATE(), NULL, NULL),
(3, N'Workflow', N'TemplateId', N'NA', N'107973', 1, N'System', GETDATE(), NULL, NULL)



