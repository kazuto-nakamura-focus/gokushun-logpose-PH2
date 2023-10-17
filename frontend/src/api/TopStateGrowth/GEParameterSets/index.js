import axios from "@/lib/axiosHooks";

//生育推定パラメータセットリスト取得
const useGrowthParamSetList = (modelId) => {
  const config = {
    params: {modelId},
  };
  return axios.get("/paramSet/list", config);
};

//生育推定
const useGrowthParamDefaultId = (modelId, deviceId, year) => {
  const config = {
    params: {modelId,deviceId,year},
  };
  return axios.get("/paramSet/defaultId", config);
};

//生育推定パラメータセット詳細取得
const useGrowthParamSetDetail = (paramSetId) => {
  const config = {
    params: {
      paramSetId,
    },
  };
  return axios.get("/growth/paramSet/detail", config);
};

//パラメータによる生育推定モデルグラフデータ取得（未使用）
const useGrowthGraphByValue = (fieldId, year, bd, be, ad, ae) => {
  const config = {
    params: { fieldId, year, b_d: bd, b_e: be, a_d: ad, a_e: ae },
  };
  return axios.get("/growth/graph/byValue", config);
};

//生育推定パラメータセット更新履歴取得
const useGrowthParamSetHistory = (paramSetId) => {
  const config = {
    params: { paramSetId },
  };
  return axios.get("/paramSet/history", config);
};

//生育推定パラメータセット更新
const useGrowthParamSetUpdate = (data) => {
  const config = {
    params: {},
  };
  return axios.put("/growth/paramSet", data, config);
};

//生育推定パラメータセット追加
const useGrowthParamSetAdd = (data) => {
  const config = {
    params: {
    },
  };
  return axios.post("/growth/paramSet", data, config);
};

//生育推定パラメータセット削除
const useGrowthParamSetRemove = (paramSetId) => {
  const config = {
    params: {
      paramSetId,
    },
  };
  return axios.delete("/growth/paramSet", config);
};

//生育推定パラメータセットデフォルト設定
const useGrowthParamSetDefault = (data) => {
  const config = {
    params: {},
  };
  return axios.put("/growth/paramSet/default", data, config);
};


export {
  useGrowthParamSetList,
  useGrowthParamDefaultId,
  useGrowthParamSetDetail,
  useGrowthGraphByValue,
  useGrowthParamSetUpdate,
  useGrowthParamSetAdd,
  useGrowthParamSetRemove,
  useGrowthParamSetHistory,
  useGrowthParamSetDefault,
};
