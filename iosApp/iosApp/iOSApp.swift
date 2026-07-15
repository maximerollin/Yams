import SwiftUI
import FirebaseAnalytics
import FirebaseCore
import FirebaseCrashlytics
import NSExceptionKtCrashlytics
import YamsApp

@main
struct iOSApp: App {
    init() {
        FirebaseApp.configure()

        #if DEBUG
        Analytics.setAnalyticsCollectionEnabled(false)
        Crashlytics.crashlytics().setCrashlyticsCollectionEnabled(false)
        #else
        Analytics.setAnalyticsCollectionEnabled(true)
        Crashlytics.crashlytics().setCrashlyticsCollectionEnabled(true)
        NSExceptionKt.addReporter(.crashlytics(causedByStrategy: .append))
        #endif

        IOSAnalytics.configure()
    }

    var body: some Scene {
        WindowGroup {
            ContentView()
        }
    }
}
