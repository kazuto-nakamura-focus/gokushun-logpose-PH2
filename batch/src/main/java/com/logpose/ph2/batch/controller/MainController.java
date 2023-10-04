package com.logpose.ph2.batch.controller;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.logpose.ph2.batch.service.BaseDataGeneratorService;
import com.logpose.ph2.batch.service.DailyBaseDataGeneratorService;
import com.logpose.ph2.batch.service.ModelDataApplyrService;
import com.logpose.ph2.batch.service.ModelDataGeneratorService;

@Component
public class MainController
	{
	private static Logger LOGGER = LogManager.getLogger(MainController.class);

	@Autowired
	private BaseDataGeneratorService baseDataGeneratorService;
	@Autowired
	private DailyBaseDataGeneratorService dailyBaseDataGeneratorService;
	@Autowired
	private ModelDataGeneratorService modelDataGeneratorService;
	@Autowired
	private ModelDataApplyrService modelDataApplyrService;

	@Scheduled(fixedRate = 600000)
	public void fetchSigfoxData()
		{
		LOGGER.info("基礎データテーブル作成開始");
		try
			{
			while (true)
				{
				if (false == this.baseDataGeneratorService.doService())
					break;
				}
			LOGGER.info("基礎データテーブル作成終了");
			}
		catch (Exception e)
			{
			e.printStackTrace();
			}
		}

	// @Scheduled(cron = "0 0 0 * * *", zone = "Asia/Tokyo")
	/*@Scheduled(fixedRate = 1200000)
	//@Scheduled(fixedDelay = 86400000)
	public void createModel()
		{
		try
			{
			this.dailyBaseDataGeneratorService.doService();
			LOGGER.info("デイリー基礎データテーブル作成終了");
			List<Long> devceList = this.modelDataGeneratorService.doService();
			LOGGER.info("モデル基礎データテーブル作成終了");
			this.modelDataApplyrService.doService(devceList);
			LOGGER.info("モデルデータテーブル作成終了");
			}
		catch (Exception e)
			{
			e.printStackTrace();
			}
		}*/

	}
