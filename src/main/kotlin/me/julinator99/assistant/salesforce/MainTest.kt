package me.julinator99.assistant.salesforce

import com.google.gson.JsonParser
import org.apache.hc.client5.http.classic.methods.HttpGet
import org.apache.hc.client5.http.classic.methods.HttpPost
import org.apache.hc.client5.http.entity.UrlEncodedFormEntity
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder
import org.apache.hc.core5.http.HttpStatus
import org.apache.hc.core5.http.NameValuePair
import org.apache.hc.core5.http.io.entity.EntityUtils
import org.apache.hc.core5.http.message.BasicNameValuePair
import java.io.File



const val LOGINURL = "https://ap5.salesforce.com"
const val GRANTSERVICE = "/services/oauth2/token"

private val sensitiveDataFile: File = TODO("Path to sensitive data file.")
private val sensitiveData = JsonParser.parseReader(sensitiveDataFile.reader()).asJsonObject
private val username = sensitiveData["username"].asString
private val rawPassword = sensitiveData["password"].asString
private val securityToken = sensitiveData["security_token"].asString
private val clientId = sensitiveData["client_id"].asString
private val clientSecret = sensitiveData["client_secret"].asString
private val password = "$rawPassword$securityToken"

fun main() {
	val httpClient = HttpClientBuilder.create().build()
	val loginURL = LOGINURL + GRANTSERVICE
	println(loginURL)
	
	val httpGet = HttpGet(loginURL)
	val httpPost = HttpPost(httpGet.uri)
	val params: MutableList<NameValuePair> = ArrayList()
	params.add(BasicNameValuePair("grant_type", "password"))
	params.add(BasicNameValuePair("client_id", clientId))
	params.add(BasicNameValuePair("client_secret", clientSecret))
	params.add(BasicNameValuePair("username", username))
	params.add(BasicNameValuePair("password", password))
	httpPost.entity = UrlEncodedFormEntity(params)
	httpClient.execute(httpPost) { response ->
		val statusCode = response.code
		if (statusCode != HttpStatus.SC_OK) throw RuntimeException("Failed to authenticate: $statusCode")
		
		val result = EntityUtils.toString(response.entity)
		
		println("Content: " + response.entity.content)
		println("Result: $result")
		
		val jsonObject = JsonParser.parseString(result).asJsonObject
		val loginAccessToken = jsonObject.get("access_token").asString
		val loginInstanceUrl = jsonObject.get("instance_url").asString
		
		println("Successful login")
		println("Instance URL: $loginInstanceUrl")
		println("Access token: $loginAccessToken")
		
		httpPost.reset()
		true
	}
}