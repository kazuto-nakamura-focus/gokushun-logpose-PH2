import axios from "@/lib/axiosHooks";

//生育推定実績値取得 
const useGrowthFData = (date, deviceId) => {
  const config = {
    params: { date, deviceId },
  };
  return axios.get("/growth/F/data", config);
};

//日付からF値の情報を得る 
const useGrowthFDataByDate = (id, date) => {
  const config = {
    params: { id, date },
  };
  return axios.get("/growth/F/date", config);
};

//生育推定実績値更新
const useGrowthFDataUpdate = (data) => {
  const config = {
    params: {
    },
  };
  return axios.put("/growth/F/data", data, config);
};

export { useGrowthFData, useGrowthFDataByDate, useGrowthFDataUpdate };
