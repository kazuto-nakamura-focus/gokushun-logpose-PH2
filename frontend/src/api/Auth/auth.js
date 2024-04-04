import axios from "@/lib/axiosHooks";

//モデル選択情報取得
const useSessionCheck = () => {
  return axios.get("/auth/session/check");
};

const useGetUser = () => {
  return axios.get("/auth/user");
};


export { useSessionCheck, useGetUser };
