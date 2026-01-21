# Security Policy

## Overview

ZimbaBeats is committed to ensuring the security and privacy of our users, especially children who use our platform. This document outlines our security practices, supported versions, and vulnerability reporting procedures.

## Supported Versions

We provide security updates for the following versions:

| Version | Supported          |
| ------- | ------------------ |
| 1.0.x   | :white_check_mark: |
| < 1.0   | :x:                |

We strongly recommend always using the latest version of ZimbaBeats to benefit from the most recent security updates and features.

## Security Measures

### Child Safety
- **Content Filtering**: All content is filtered through multiple layers to ensure age-appropriateness
- **Safe Search**: Enabled by default with parental override controls
- **No Direct Messaging**: Children cannot receive or send messages through the app
- **No User-Generated Content**: Children cannot post, comment, or share content

### Data Protection
- **Firebase Authentication**: Secure authentication using Google's Firebase Auth
- **Encrypted Communications**: All API communications use HTTPS/TLS encryption
- **Minimal Data Collection**: We only collect data necessary for app functionality
- **No Third-Party Tracking**: We do not share user data with advertisers or third parties
- **Local Preferences**: User preferences are stored locally on the device

### Parental Controls (ZimbaBeats Family)
- **PIN Protection**: Family app settings are protected by PIN/biometric authentication
- **Real-time Sync**: Parental control settings sync in real-time via Firebase
- **Device Management**: Parents can link, monitor, and unlink child devices
- **Activity History**: Parents can view their children's listening history
- **Secure Pairing**: 6-character pairing codes expire after use

### Infrastructure Security
- **Firebase Firestore**: Secure, real-time database with strict security rules
- **UID-based Access Control**: Users can only access their own family data
- **No Server-Side Storage**: No sensitive data is stored on our API servers
- **Rate Limiting**: API endpoints are protected against abuse

## Reporting a Vulnerability

We take security vulnerabilities seriously. If you discover a security issue, please report it responsibly.

### How to Report

1. **Email**: Send details to [security@zimbabeats.com](mailto:security@zimbabeats.com)
2. **GitHub**: Open a private security advisory at [GitHub Security](https://github.com/raveuk/ZimbaBeats/security/advisories/new)

### What to Include

When reporting a vulnerability, please include:
- A clear description of the vulnerability
- Steps to reproduce the issue
- Potential impact assessment
- Any proof-of-concept code (if applicable)
- Your contact information for follow-up

### What to Expect

1. **Acknowledgment**: We will acknowledge receipt within 48 hours
2. **Assessment**: We will investigate and assess the severity within 7 days
3. **Resolution**: Critical vulnerabilities will be addressed within 14 days
4. **Notification**: You will be notified when the issue is resolved
5. **Credit**: With your permission, we will credit you in our release notes

### Scope

The following are in scope for security reports:
- ZimbaBeats Android application
- ZimbaBeats Family Android application
- ZimbaBeats Web Player (zimbabeats.com)
- ZimbaBeats Family Web Portal
- ZimbaBeats API (zimbabeats-api.onrender.com)

The following are out of scope:
- Third-party services (YouTube, Firebase, Cloudflare)
- Social engineering attacks
- Denial of service attacks
- Issues requiring physical access to a device

## Security Best Practices for Users

### For Parents
1. **Use Strong Passwords**: Create unique, strong passwords for your account
2. **Enable Biometric Lock**: Use fingerprint or face recognition when available
3. **Review Settings Regularly**: Periodically check your parental control settings
4. **Keep Apps Updated**: Always update to the latest version
5. **Monitor Device Access**: Be aware of who has access to linked devices

### For All Users
1. **Download from Official Sources**: Only install from Google Play or our official website
2. **Don't Share Pairing Codes**: Keep pairing codes private
3. **Report Suspicious Content**: Use in-app reporting for any concerning content
4. **Log Out on Shared Devices**: Always sign out when using shared devices

## Compliance

ZimbaBeats is designed with privacy regulations in mind:
- **COPPA Compliant**: We follow the Children's Online Privacy Protection Act guidelines
- **GDPR Aware**: We respect European data protection standards
- **No Ads for Children**: The child app is completely ad-free

## Contact

For general security inquiries:
- Email: [security@zimbabeats.com](mailto:security@zimbabeats.com)
- GitHub Issues: [github.com/raveuk/ZimbaBeats/issues](https://github.com/raveuk/ZimbaBeats/issues)

For urgent security matters, please use the email address above with "URGENT" in the subject line.

---

*Last Updated: January 2025*
