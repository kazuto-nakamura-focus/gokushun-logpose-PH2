package com.logpose.ph2.api.configration;

import lombok.Data;

@Data
public class DefaultHikariParameter
	{
	Integer maxLifetime1;
	Integer minimumIdle1;
	Integer maximumPoolSize1;
	Integer maxLifetime2;
	Integer minimumIdle2;
	Integer maximumPoolSize2;
	}
