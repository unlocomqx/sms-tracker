use tauri::{command, AppHandle, Runtime};

use crate::{PermissionType, SmsExt};
use crate::{PermissionStatus, Result};
use crate::{SendSmsPayload, SendSmsStatus};

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
    payload: Option<SendSmsPayload>,
) -> Result<SendSmsStatus> {
    app.sms().send_sms(payload)
}
