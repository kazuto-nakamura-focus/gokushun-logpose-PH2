import axios from "@/lib/axiosHooks";

//生育推定F値データ取得
const useGrowthFAll = (year, deviceId) => {
  const config = {
    params: { year, deviceId },
  };
  return axios.get("/growth/F/all", config);
};

export { useGrowthFAll };
