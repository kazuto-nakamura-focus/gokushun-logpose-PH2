import axios from "@/lib/axiosHooks";

//ログアウト
const useLogout = () => {
  const config = {
    params: {},
  };
  return axios.get("/auth/logout", config);
};


export {
  useLogout,
};
