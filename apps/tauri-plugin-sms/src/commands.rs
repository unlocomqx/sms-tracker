use tauri::{command, AppHandle, Runtime};

use crate::{PermissionType, SmsExt};
use crate::{PermissionStatus, Result};

#[command]
pub(crate) async fn check_permissions<R: Runtime>(app: AppHandle<R>) -> Result<PermissionStatus> {
    app.sms().check_permissions()
}

#[command]
pub(crate) async fn request_permissions<R: Runtime>(
    app: AppHandle<R>,
    permissions: Option<Vec<PermissionType>>,
) -> Result<PermissionStatus> {
    app.sms().request_permissions(permissions)
}

#[command]
pub(crate) async fn send_sms<R: Runtime>(
    app: AppHandle<R>,
    to: Option<Vec<PermissionType>>,
) -> Result<PermissionStatus> {
    app.sms().send_sms(permissions)
}
