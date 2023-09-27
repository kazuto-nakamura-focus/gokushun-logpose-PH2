import axios from "@/lib/axiosHooks";

//パラメータセットリスト取得
const useParamSetList = (modelId) => {
  const config = {
    params: {modelId},
  };
  return axios.get("/paramSet/list", config);
};

// デフォルトID取得
const useParamDefaultId = (modelId, deviceId, year) => {
  const config = {
    params: {modelId,deviceId,year},
  };
  return axios.get("/paramSet/defaultId", config);
};


//更新履歴取得
const useParamSetHistory = (paramSetId) => {
  const config = {
    params: { paramSetId },
  };
  return axios.get("/paramSet/history", config);
};


//パラメータセット削除
const useParamSetDelete = (paramSetId) => {
  const config = {
    params: {
      paramSetId,
    },
  };
  return axios.delete("/paramSet/delete", config);
};

export {
  useParamSetList,
  useParamDefaultId,
  useParamSetHistory,
  useParamSetDelete,
};
