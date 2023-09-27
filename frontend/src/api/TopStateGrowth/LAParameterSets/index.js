import axios from "@/lib/axiosHooks";

//葉面積・葉枚数パラメータセットリスト取得
const useLeafParamSetList = () => {
  const config = {
    params: {modelId : 2},
  };
  return axios.get("paramSet/list", config);
};

//葉面積・葉枚数パラメータセットデフォルト
const useLeafParamDefaultId = (modelId, deviceId, year) => {
  const config = {
    params: {modelId,deviceId,year},
  };
  return axios.get("/paramSet/defaultId", config);
};

//葉面積・葉枚数パラメータセット詳細取得
const useLeafParamSetDetail = (paramSetId) => {
  const config = {
    params: {
      paramSetId,
    },
  };
  return axios.get("/leaf/paramSet/detail", config);
};

//葉面積・葉枚数パラメータセット更新履歴取得
const useLeafParamSetHistory = (paramSetId) => {
  const config = {
    params: { paramSetId },
  };
  return axios.get("/paramSet/history", config);
};

//葉面積・葉枚数パラメータセット更新
const useLeafParamSetUpdate = (data) => {
  const config = {
    params: {},
  };
  return axios.put("/leaf/paramSet", data, config);
};

//葉面積・葉枚数パラメータセット追加
const useLeafParamSetAdd = (data) => {
  const config = {
    params: {},
  };
  return axios.post("/leaf/paramSet", data, config);
};

//葉面積・葉枚数パラメータセット削除
const useLeafParamSetRemove = (paramSetId) => {
  const config = {
    params: {
      paramSetId,
    },
  };
  return axios.delete("/leaf/paramSet", config);
};

export {
  useLeafParamSetList,
  useLeafParamDefaultId,
  useLeafParamSetDetail,
  useLeafParamSetHistory,
  useLeafParamSetUpdate,
  useLeafParamSetAdd,
  useLeafParamSetRemove,
};
