import ExpoModulesCore
import Photos

public class MediaLibraryPermissionRequester: DefaultMediaLibraryPermissionRequester, EXPermissionsRequester {
  public static func permissionType() -> String {
    return "mediaLibrary"
  }
}

public class MediaLibraryWriteOnlyPermissionRequester: DefaultMediaLibraryPermissionRequester, EXPermissionsRequester {
  public static func permissionType() -> String {
    return "mediaLibraryWriteOnly"
  }

  @available(iOS 14, *)
  override internal func accessLevel() -> PHAccessLevel {
    return PHAccessLevel.addOnly
  }
}

public class DefaultMediaLibraryPermissionRequester: NSObject {}

extension DefaultMediaLibraryPermissionRequester {
  @objc
  public func requestPermissions(resolver resolve: @escaping EXPromiseResolveBlock, rejecter reject: EXPromiseRejectBlock) {
      if #available(iOS 14, *) {
          let authorizationHandler = { (_: PHAuthorizationStatus) in
              resolve(self.getPermissions())
          }
          PHPhotoLibrary.requestAuthorization(for: self.accessLevel(), handler: authorizationHandler)
      } else {
          PHPhotoLibrary.requestAuthorization { (_: PHAuthorizationStatus) in
              resolve(self.getPermissions())
          }
      }
  }

  @objc
  public func getPermissions() -> [AnyHashable: Any] {
      if #available(iOS 14, *) {
          let authorizationStatus = PHPhotoLibrary.authorizationStatus(for: self.accessLevel())
          var status: EXPermissionStatus
          var scope: String

          switch authorizationStatus {
          case .authorized:
              status = EXPermissionStatusGranted
              scope = "all"
          case .limited:
              status = EXPermissionStatusGranted
              scope = "limited"
          case .denied, .restricted:
              status = EXPermissionStatusDenied
              scope = "none"
          case .notDetermined:
              fallthrough
          @unknown default:
              status = EXPermissionStatusUndetermined
              scope = "none"
          }

          return [
              "status": status.rawValue,
              "accessPrivileges": scope,
              "granted": status == EXPermissionStatusGranted
          ]
      } else {
          // For iOS 13.4 and 13.5
          let authorizationStatus = PHPhotoLibrary.authorizationStatus()
          var status: EXPermissionStatus
          let scope = "all" // iOS 13 doesn't have limited access

          switch authorizationStatus {
          case .authorized:
              status = EXPermissionStatusGranted
          case .denied, .restricted:
              status = EXPermissionStatusDenied
          case .notDetermined:
              fallthrough
          @unknown default:
              status = EXPermissionStatusUndetermined
          }

          return [
              "status": status.rawValue,
              "accessPrivileges": scope,
              "granted": status == EXPermissionStatusGranted
          ]
      }
  }

  @available(iOS 14, *)
  @objc
  internal func accessLevel() -> PHAccessLevel {
    return PHAccessLevel.readWrite
  }
}
