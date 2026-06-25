import SwiftUI

@main
struct iOSApp: App {
    init() {
        IOSAnalytics.configure()
    }

    var body: some Scene {
        WindowGroup {
            ContentView()
        }
    }
}
