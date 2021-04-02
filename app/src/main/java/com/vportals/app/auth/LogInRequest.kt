package com.vportals.app.auth

data class LogInRequest(var PortalNumber: String, var UserName: String, var Password: String, var deviceToken: String)
