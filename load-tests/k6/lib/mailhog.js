import http from 'k6/http';
import { sleep } from 'k6';
import { MAILHOG_URL } from './config.js';

export function pollVerificationToken(email, {
  attempts = 20,
  intervalSeconds = 0.5,
} = {}) {
  for (let attempt = 0; attempt < attempts; attempt += 1) {
    const response = http.get(`${MAILHOG_URL}/api/v2/messages?limit=200`, {
      tags: {
        service: 'mailhog',
        endpoint: 'messages',
        flow: 'email_verification',
        operation: 'read',
        method: 'GET',
      },
    });

    if (response.status === 200) {
      const token = findVerificationToken(response, email);
      if (token) {
        return token;
      }
    }

    sleep(intervalSeconds);
  }

  return null;
}

function findVerificationToken(response, email) {
  const body = response.json();
  const items = body.items || [];

  for (const item of items) {
    const recipients = item.To || [];
    const sentToTarget = recipients.some((recipient) => recipient.Mailbox && recipient.Domain
      && `${recipient.Mailbox}@${recipient.Domain}` === email);
    const content = item.Content && item.Content.Body ? item.Content.Body : '';
    const match = content.match(/verifyEmail\/([0-9a-fA-F-]{36})/);

    if (sentToTarget && match) {
      return match[1];
    }
  }

  return null;
}

