package com.saas.saas.util;

public class UserAgentUtil {

    private UserAgentUtil() {

    }

    public static String getBrowser(
            String userAgent
    ) {

        if (userAgent == null) {
            return "Unknown";
        }

        if (userAgent.contains("Edg")) {
            return "Edge";
        }

        if (userAgent.contains("Chrome")) {
            return "Chrome";
        }

        if (userAgent.contains("Firefox")) {
            return "Firefox";
        }

        if (userAgent.contains("Safari")) {
            return "Safari";
        }

        return "Unknown";
    }

    public static String getOperatingSystem(
            String userAgent
    ) {

        if (userAgent == null) {
            return "Unknown";
        }

        if (userAgent.contains("Windows")) {
            return "Windows";
        }

        if (userAgent.contains("Android")) {
            return "Android";
        }

        if (userAgent.contains("Mac")) {
            return "macOS";
        }

        if (userAgent.contains("Linux")) {
            return "Linux";
        }

        return "Unknown";
    }

}