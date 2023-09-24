import axios from "@/lib/axiosHooks";

//モデル選択情報取得
const useModels = () => {
  const config = {
    params: {},
  };
  return axios.get("/models", config);
};

export { useModels };
