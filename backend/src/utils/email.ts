import nodemailer from 'nodemailer';
import type { Transporter } from 'nodemailer';

/**
 * Email service for sending authentication and transactional emails
 * Uses Postmark SMTP for reliable delivery
 */

interface EmailOptions {
  to: string;
  subject: string;
  html: string;
  text?: string;
}

interface VerificationEmailData {
  userName: string;
  verificationLink: string;
}

interface PasswordResetEmailData {
  userName: string;
  resetLink: string;
}

// Initialize transporter
let transporter: Transporter | null = null;

function getTransporter(): Transporter {
  if (!transporter) {
    transporter = nodemailer.createTransport({
      host: process.env.SMTP_HOST || 'smtp.postmarkapp.com',
      port: parseInt(process.env.SMTP_PORT || '587'),
      secure: false, // Use TLS
      auth: {
        user: process.env.SMTP_USERNAME,
        pass: process.env.SMTP_PASSWORD,
      },
    });
  }
  return transporter;
}

/**
 * Send email using Postmark SMTP
 */
export async function sendEmail(options: EmailOptions): Promise<void> {
  const transport = getTransporter();
  
  const fromName = process.env.SMTP_FROM_NAME || 'BlueDiesel Refill Kiosk';
  const fromEmail = process.env.SMTP_FROM_EMAIL || 'noreply@interion.com.sg';

  try {
    const info = await transport.sendMail({
      from: `"${fromName}" <${fromEmail}>`,
      to: options.to,
      subject: options.subject,
      text: options.text,
      html: options.html,
    });

    console.log('✅ Email sent successfully:', {
      messageId: info.messageId,
      to: options.to,
      subject: options.subject,
    });
  } catch (error) {
    console.error('❌ Failed to send email:', error);
    throw new Error('Failed to send email');
  }
}

/**
 * Send email verification email
 */
export async function sendVerificationEmail(
  email: string,
  data: VerificationEmailData
): Promise<void> {
  const appName = process.env.APP_NAME || 'BlueDiesel Refill Kiosk';
  
  const html = `
<!DOCTYPE html>
<html>
<head>
  <meta charset="utf-8">
  <style>
    body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }
    .container { max-width: 600px; margin: 0 auto; padding: 20px; }
    .header { background: #082E44; color: white; padding: 20px; text-align: center; border-radius: 5px 5px 0 0; }
    .content { background: #f9f9f9; padding: 30px; border: 1px solid #ddd; }
    .button { 
      display: inline-block; 
      background: #082E44; 
      color: white; 
      padding: 12px 30px; 
      text-decoration: none; 
      border-radius: 5px; 
      margin: 20px 0;
    }
    .footer { text-align: center; padding: 20px; color: #666; font-size: 12px; }
  </style>
</head>
<body>
  <div class="container">
    <div class="header">
      <h1>${appName}</h1>
    </div>
    <div class="content">
      <h2>Welcome, ${data.userName}!</h2>
      <p>Thank you for registering with ${appName}. To complete your account setup, please verify your email address by clicking the button below:</p>
      
      <div style="text-align: center;">
        <a href="${data.verificationLink}" class="button">Verify Email Address</a>
      </div>
      
      <p>Or copy and paste this link into your browser:</p>
      <p style="word-break: break-all; color: #082E44;">${data.verificationLink}</p>
      
      <p><strong>This link will expire in 24 hours.</strong></p>
      
      <p>If you didn't create an account with ${appName}, you can safely ignore this email.</p>
    </div>
    <div class="footer">
      <p>&copy; 2026 ${appName}. All rights reserved.</p>
      <p>Need help? Contact us at info@bluediesel.com.my</p>
    </div>
  </div>
</body>
</html>
  `.trim();

  const text = `
Welcome to ${appName}!

Hi ${data.userName},

Thank you for registering. Please verify your email address by clicking the link below:

${data.verificationLink}

This link will expire in 24 hours.

If you didn't create an account, you can safely ignore this email.

---
${appName}
info@bluediesel.com.my
  `.trim();

  await sendEmail({
    to: email,
    subject: `Verify your ${appName} account`,
    html,
    text,
  });
}

/**
 * Send password reset email
 */
export async function sendPasswordResetEmail(
  email: string,
  data: PasswordResetEmailData
): Promise<void> {
  const appName = process.env.APP_NAME || 'BlueDiesel Refill Kiosk';
  
  const html = `
<!DOCTYPE html>
<html>
<head>
  <meta charset="utf-8">
  <style>
    body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }
    .container { max-width: 600px; margin: 0 auto; padding: 20px; }
    .header { background: #082E44; color: white; padding: 20px; text-align: center; border-radius: 5px 5px 0 0; }
    .content { background: #f9f9f9; padding: 30px; border: 1px solid #ddd; }
    .button { 
      display: inline-block; 
      background: #082E44; 
      color: white; 
      padding: 12px 30px; 
      text-decoration: none; 
      border-radius: 5px; 
      margin: 20px 0;
    }
    .footer { text-align: center; padding: 20px; color: #666; font-size: 12px; }
    .warning { background: #FFF3CD; border: 1px solid #FFE69C; padding: 10px; border-radius: 5px; margin: 15px 0; }
  </style>
</head>
<body>
  <div class="container">
    <div class="header">
      <h1>${appName}</h1>
    </div>
    <div class="content">
      <h2>Password Reset Request</h2>
      <p>Hi ${data.userName},</p>
      <p>We received a request to reset your password for your ${appName} account. Click the button below to create a new password:</p>
      
      <div style="text-align: center;">
        <a href="${data.resetLink}" class="button">Reset Password</a>
      </div>
      
      <p>Or copy and paste this link into your browser:</p>
      <p style="word-break: break-all; color: #082E44;">${data.resetLink}</p>
      
      <div class="warning">
        <p><strong>⚠️ Security Notice:</strong></p>
        <ul>
          <li>This link will expire in 1 hour</li>
          <li>If you didn't request a password reset, please ignore this email</li>
          <li>Your password will remain unchanged unless you click the link above</li>
        </ul>
      </div>
      
      <p>If you're having trouble, contact our support team at info@bluediesel.com.my</p>
    </div>
    <div class="footer">
      <p>&copy; 2026 ${appName}. All rights reserved.</p>
      <p>This is an automated security email. Please do not reply.</p>
    </div>
  </div>
</body>
</html>
  `.trim();

  const text = `
Password Reset Request - ${appName}

Hi ${data.userName},

We received a request to reset your password. Click the link below to create a new password:

${data.resetLink}

⚠️ SECURITY NOTICE:
- This link expires in 1 hour
- If you didn't request this, ignore this email
- Your password remains unchanged unless you click the link

Need help? Contact us at info@bluediesel.com.my

---
${appName}
This is an automated security email.
  `.trim();

  await sendEmail({
    to: email,
    subject: `Reset your ${appName} password`,
    html,
    text,
  });
}

/**
 * Test email configuration
 */
export async function testEmailConfiguration(): Promise<boolean> {
  try {
    const transport = getTransporter();
    await transport.verify();
    console.log('✅ Email configuration is valid');
    return true;
  } catch (error) {
    console.error('❌ Email configuration error:', error);
    return false;
  }
}
