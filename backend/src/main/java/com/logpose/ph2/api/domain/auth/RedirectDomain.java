package com.logpose.ph2.api.domain.auth;

import java.io.IOException;
import java.io.PrintWriter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.logpose.ph2.api.configration.DefaultDomainParameter;
import com.logpose.ph2.api.dto.ResponseDTO;
import com.logpose.ph2.api.master.CookieMaster;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class RedirectDomain
	{
	@Autowired
	private DefaultDomainParameter param;

	public void setTokenCookie(HttpServletResponse response, String name)
		{
		Cookie atc = new Cookie(CookieMaster.ACCESS_TOKEN, name);
		atc.setMaxAge(7776000);
		atc.setPath("/");
		atc.setDomain(this.param.getDomain());
		response.addCookie(atc);
		}

	public ResponseDTO sendRedirect(HttpServletResponse response, String url, ResponseDTO dto) throws IOException
		{
		Cookie atc = new Cookie(CookieMaster.ACCESS_TOKEN, null);
		atc.setMaxAge(0);
		atc.setPath("/");
		atc.setDomain(this.param.getDomain());
		response.addCookie(atc);

		response.setStatus(HttpServletResponse.SC_OK);
		/*response.addHeader("Access-Control-Allow-Origin", this.param.getDomain());
		response.addHeader("Access-Control-Allow-Credentials", "true");
		response.addHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, PUT, DELETE, HEAD");
		response.addHeader("Access-Control-Allow-Headers",
				"X-PINGOTHER, Origin, X-Requested-With, Content-Type, Accept");
		response.addHeader("Access-Control-Max-Age", "1728000");*/

		ResponseDTO mssg = dto;
		if (null == mssg) mssg = new ResponseDTO();
		mssg.setRedirect(url);

		if (null == dto)
			{
			ObjectMapper mapper = new ObjectMapper();
			String jsonData = mapper.writeValueAsString(mssg);

			PrintWriter out = response.getWriter();
			response.setContentType("application/json");
			response.setCharacterEncoding("UTF-8");
			out.print(jsonData);
			out.flush();
			out.close();
			}
		return mssg;
		}
	}
