import Foundation
import PostHog
import YamsApp

enum IOSAnalytics {
    private static let applicationInstalledTrackedKey = "application_installed_tracked"
    private static var isConfigured = false

    static func configure() {
        guard !isConfigured else { return }
        isConfigured = true

        let buildConfig = YamsBuildConfig.shared
        let apiKey = buildConfig.POSTHOG_API_KEY.trimmingCharacters(in: .whitespacesAndNewlines)

        if !apiKey.isEmpty {
            configurePostHog(apiKey: apiKey, host: buildConfig.POSTHOG_HOST)
        }

        IosAnalyticsBridge.shared.configure(
            releaseChannel: buildConfig.ANALYTICS_RELEASE_CHANNEL,
            postHogCapture: { event, properties in
                guard !apiKey.isEmpty else { return }
                PostHogSDK.shared.capture(event, properties: properties)
            },
            logSnagCapture: { event, tags, insightTitle in
                LogSnagClient.capture(
                    token: buildConfig.LOGSNAG_TOKEN,
                    project: buildConfig.LOGSNAG_PROJECT,
                    event: event,
                    tags: tags,
                    insightTitle: insightTitle
                )
            }
        )

        if !apiKey.isEmpty {
            captureApplicationInstalledIfNeeded()
        }
    }

    private static func configurePostHog(apiKey: String, host: String) {
        let config = PostHogConfig(projectToken: apiKey, host: host)
        config.captureApplicationLifecycleEvents = false
        config.captureScreenViews = false
        config.captureElementInteractions = false
        config.sessionReplay = false
        config.surveys = false
        config.enableSwizzling = false
        PostHogSDK.shared.setup(config)
    }

    private static func captureApplicationInstalledIfNeeded() {
        let defaults = UserDefaults.standard
        guard !defaults.bool(forKey: applicationInstalledTrackedKey) else { return }

        let info = Bundle.main.infoDictionary
        IosAnalyticsBridge.shared.captureApplicationInstalled(
            appVersion: info?["CFBundleShortVersionString"] as? String,
            appBuild: info?["CFBundleVersion"] as? String
        )
        defaults.set(true, forKey: applicationInstalledTrackedKey)
    }
}

private enum LogSnagClient {
    private static let userIDKey = "logsnag_user_id"
    private static let logURL = URL(string: "https://api.logsnag.com/v1/log")!
    private static let insightURL = URL(string: "https://api.logsnag.com/v1/insight")!
    private static var userID: String {
        let defaults = UserDefaults.standard
        if let existingUserID = defaults.string(forKey: userIDKey)?
            .trimmingCharacters(in: .whitespacesAndNewlines),
           !existingUserID.isEmpty {
            return existingUserID
        }

        let generatedUserID = "ios-\(UUID().uuidString.lowercased())"
        defaults.set(generatedUserID, forKey: userIDKey)
        return generatedUserID
    }

    static func capture(
        token: String,
        project: String,
        event: String,
        tags: [String: String],
        insightTitle: String?
    ) {
        let cleanToken = token.trimmingCharacters(in: .whitespacesAndNewlines)
        let cleanProject = project.trimmingCharacters(in: .whitespacesAndNewlines)
        guard !cleanToken.isEmpty, !cleanProject.isEmpty else { return }

        send(
            url: logURL,
            method: "POST",
            token: cleanToken,
            body: [
                "project": cleanProject,
                "channel": "analytics",
                "event": event,
                "user_id": userID,
                "tags": tags,
                "notify": false,
            ]
        )

        if let insightTitle {
            send(
                url: insightURL,
                method: "PATCH",
                token: cleanToken,
                body: [
                    "project": cleanProject,
                    "title": insightTitle,
                    "value": ["$inc": 1],
                ]
            )
        }
    }

    private static func send(
        url: URL,
        method: String,
        token: String,
        body: [String: Any]
    ) {
        guard let data = try? JSONSerialization.data(withJSONObject: body) else { return }

        var request = URLRequest(url: url)
        request.httpMethod = method
        request.httpBody = data
        request.setValue("Bearer \(token)", forHTTPHeaderField: "Authorization")
        request.setValue("application/json", forHTTPHeaderField: "Content-Type")
        URLSession.shared.dataTask(with: request).resume()
    }
}
