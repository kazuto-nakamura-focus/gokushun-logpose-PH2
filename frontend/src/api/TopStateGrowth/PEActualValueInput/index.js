import axios from "@/lib/axiosHooks";

//光合成推定実績取得
const usePhotosynthesisValuesDetail = (deviceId, year) => {
  const config = {
    params: {
      deviceId,
      year
    },
  };
  return axios.get("/photosynthesis/values", config);
};

//光合成推定実績値更新
const usePhotosynthesisValuesUpdate = (data) => {
  const config = {
    params: {},
  };
  return axios.put("/photosynthesis/values", data, config);
};

export {
  usePhotosynthesisValuesDetail,
  usePhotosynthesisValuesUpdate,
};
