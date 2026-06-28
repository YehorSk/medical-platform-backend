package com.yehorsk.medicalplatformbackend.common.service

import org.springframework.beans.factory.annotation.Value
import org.springframework.mail.SimpleMailMessage
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.MimeMessageHelper
import org.springframework.stereotype.Service

@Service
class MailService(
    private val mailSender: JavaMailSender,
    @param:Value($$"${spring.mail.from}")
    private val from: String
) {

    fun sendPlainText(to: String, subject: String, body: String) {
        println("Sending email to $to")
        val message = SimpleMailMessage()
        message.setTo(to)
        message.from = from
        message.subject = subject
        message.text = body
        mailSender.send(message)
    }

    fun sendHtml(to: String, subject: String, body: String) {
        val message = mailSender.createMimeMessage()
        val helper = MimeMessageHelper(message, "UTF-8")
        helper.setFrom(from)
        helper.setTo(to)
        helper.setSubject(subject)
        helper.setText(body, true)
        mailSender.send(message)
    }

}