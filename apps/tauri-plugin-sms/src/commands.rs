use tauri::{command, plugin::PermissionState, AppHandle, Runtime};

use crate::SmsExt;
use crate::{PermissionStatus, Result};

#[command]
pub(crate) async fn check_permissions<R: Runtime>(app: AppHandle<R>) -> Result<PermissionStatus> {
    app.sms().check_permissions()
}

#[command]
pub(crate) async fn request_permissions<R: Runtime>(app: AppHandle<R>) -> Result<PermissionStatus> {
    app.sms().request_permissions()
}
