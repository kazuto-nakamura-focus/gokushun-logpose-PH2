import axios from "@/lib/axiosHooks";

//光合成推定パラメータセットリスト取得
const usePhotosynthesisParamSetList = () => {
  const config = {
    params: { modelId: 3 },
  };
  return axios.get("/paramSet/list", config);
};

//光合成推定パラメータセットデフォルト
const usePhotosynthesisDefaultId = (modelId, deviceId, year) => {
  const config = {
    params: { modelId, deviceId, year },
  };
  return axios.get("/paramSet/defaultId", config);
};

//光合成推定パラメータセット詳細取得
const usePhotosynthesisParamSetDetail = (paramSetId) => {
  const config = {
    params: {
      paramSetId,
    },
  };
  return axios.get("/photosynthesis/paramSet/detail", config);
};

//光合成推定パラメータセット更新履歴取得
const usePhotosynthesisParamSetHistory = (paramSetId) => {
  const config = {
    params: { paramSetId },
  };
  return axios.get("/paramSet/history", config);
};

//光合成推定パラメータセット更新
const usePhotosynthesisParamSetUpdate = (data) => {
  const config = {
    //   params: {},
    withCredential: true,
  };
  return axios.put("/photosynthesis/paramSet", data, config);
};

//光合成推定パラメータセット追加
const usePhotosynthesisParamSetAdd = (data) => {
  const config = {
    params: {},
    withCredential: true,
  };
  return axios.post("/photosynthesis/paramSet", data, config);
};

//光合成推定パラメータセット削除
const usePhotosynthesisParamSetRemove = (paramSetId) => {
  const config = {
    params: {
      paramSetId,
    },
  };
  return axios.delete("/photosynthesis/paramSet", config);
};

//光合成推定パラメータセットデフォルト設定
const usePhotosynthesishParamSetDefault = (data) => {
  const config = {
    params: {},
  };
  return axios.put("/photosynthesis/paramSet/default", data, config);
};

export {
  usePhotosynthesisParamSetList,
  usePhotosynthesisDefaultId,
  usePhotosynthesisParamSetDetail,
  usePhotosynthesisParamSetHistory,
  usePhotosynthesisParamSetUpdate,
  usePhotosynthesisParamSetAdd,
  usePhotosynthesisParamSetRemove,
  usePhotosynthesishParamSetDefault,
};
