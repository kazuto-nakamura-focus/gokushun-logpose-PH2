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
const useFruitValue = (deviceId, targetDate, eventId) => {
  const config = {
    params: {
      deviceId,
      targetDate,
      eventId
    },
  };
  return axios.get("/fruit/value", config);
};
//圃場着果量着果負担詳細取得
const useFruitValues = (deviceId) => {
  const config = {
    params: {
      deviceId,
    },
  };
  return axios.get("/fruit/values", config);
};

//着生後芽かき処理時実績値更新
const useFruitValueSproutTreatment = (data) => {
  const config = {
    params: {},
  };
  return axios.post("/fruit/value/sproutTreatment", data, config);
};

//E-L 27～31の生育ステージ時実績値更新処理
const useFruitValueELStage = (data) => {
  const config = {
    params: {},
  };
  return axios.post("/fruit/value/ELStage", data, config);
};

//袋かけ時実績値更新処理
const useFruitValueBagging = (data) => {
  const config = {
    params: {},
  };
  return axios.post("/fruit/value/bagging", data, config);
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
  useFruitValueSproutTreatment,
  useFruitValueELStage,
  useFruitValueBagging,
};
