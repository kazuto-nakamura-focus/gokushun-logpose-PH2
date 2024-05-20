import axios from "@/lib/axiosHooks";

//生育推定モデルグラフデータ取得
const useModelData = (deviceId, year) => {
  const config = {
    params: {
      deviceId,
      year
    },
  };
  return axios.get("/growth/graph", config);
};

//生育推定モデルグラフデータ取得（未使用）
const useGrowthGraphByParamSet = (deviceId, year, paramSetId) => {
  const config = {
    params: {
      deviceId,
      year,
      paramSetId,
    },
  };
  return axios.get("/growth/graph/byParamSet", config);
};

//生育推定実績グラフデータ取得（未使用）
const useGrowthGraphReal = (deviceId, year, paramSetId) => {
  const config = {
    params: {
      deviceId,
      year,
      paramSetId,
    },
  };
  return axios.get("/growth/graph/real", config);
};

//生育推定イベントデータ取得（未使用）
const useGrowthEvent = (deviceId, year) => {
  const config = {
    params: {
      deviceId,
      year,
    },
  };
  return axios.get("/growth/event", config);
};

//葉面積モデルグラフデータ取得（未使用）
const useLeafGraphAreaByParamSet = (deviceId, year) => {
  const config = {
    params: {
      deviceId,
      year,
    },
  };
  return axios.get("/leaf/graph/area/byParamSet", config);
};

//葉面積実績グラフデータ取得
const useLeafGraphAreaReal = (deviceId, year) => {
  const config = {
    params: {
      deviceId,
      year
    },
  };
  return axios.get("/leaf/graph/area/real", config);
};

//葉枚数モデルグラフデータ取得（未使用）
const useLeafGraphCountByParamSet = (deviceId, year, paramSetId) => {
  const config = {
    params: {
      deviceId,
      year,
      paramSetId,
    },
  };
  return axios.get("/leaf/graph/count/byParamSet", config);
};

//葉枚数実績グラフデータ取得（未使用）
const useLeafGraphCountReal = (deviceId, year, paramSetId) => {
  const config = {
    params: {
      deviceId,
      year,
      paramSetId,
    },
  };
  return axios.get("/leaf/graph/count/real", config);
};

//光合成推定グラフデータ取得（未使用）
const usePhotosynthesisGraphByParamSet = (deviceId, year) => {
  const config = {
    params: {
      deviceId,
      year
    },
  };
  return axios.get("/photosynthesis/graph/byParamSet", config);
};

//光合成推定実績グラフデータ取得（未使用）
const usePhotosynthesisGraphReal = (deviceId, year, paramSetId) => {
  const config = {
    params: {
      deviceId,
      year,
      paramSetId,
    },
  };
  return axios.get("/photosynthesis/graph/real", config);
};

//着果量着果負担値取得
const useFruitValue = (deviceId, year) => {
  const config = {
    params: {
      deviceId,
      year
    },
  };
  return axios.get("/fruit/value", config);
};
//圃場着果量着果負担詳細取得
const useFruitValues = (deviceId, year) => {
  const config = {
    params: {
      deviceId,
      year
    },
  };
  return axios.get("/fruit/values", config);
};

//圃場着果量着果負担詳細取得Ver2
const useFruitDetails = (deviceId, year) => {
  const config = {
    params: {
      deviceId,
      year
    },
  };
  return axios.get("/fruit/details", config);
};

//実績値更新
const useFruitValueUpdate = (data) => {
  const config = {
    params: {},
  };
  return axios.post("/fruit/value", data, config);
};
//実績値クリア
const useFruitValueDelete = (deviceId, year, eventId) => {
  const config = {
    params: { deviceId, year, eventId },
  };
  return axios.delete("/fruit/value", config);
};

export {
  useModelData,
  useGrowthGraphByParamSet,
  useGrowthGraphReal,
  useGrowthEvent,
  useLeafGraphAreaByParamSet,
  useLeafGraphAreaReal,
  useLeafGraphCountByParamSet,
  useLeafGraphCountReal,
  usePhotosynthesisGraphByParamSet,
  usePhotosynthesisGraphReal,
  useFruitValue,
  useFruitValues,
  useFruitValueUpdate,
  useFruitDetails,
  useFruitValueDelete
};
