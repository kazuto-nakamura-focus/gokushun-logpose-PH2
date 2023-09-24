//APIリクエストのテンプレート //画面毎にAPIを作成し、リクエストを行う
//リクエストの宛先が同じでもメソッド（GET・POST・PUT・DELETE）で処理を分けることが可能
//=> RESTAPI風 //関数名は、処理内容に合わせて変えても問題ない（useGet ->useUsers、またはuseActualDataなど）
//※ベースURLは、@/lib/axiosHooksで定義しているため、関数内のURL設定はパスのみで設定する
//https://www.example.com/test => test

import axios from "@/lib/axiosHooks";

//GET方式
//クエリが必要な場合は、paramsに設定
const useGet = () => {
  const config = {
    params: {
      //?id=1234の場合、id:1234
    },
  };
  return axios.get("/", config);
};

//POST方式
//引数は処理に必要なデータ
//クエリが必要な場合は、paramsに設定
const usePost = (data) => {
  const config = {
    params: {
      //?id=1234の場合、id:1234
    },
  };
  return axios.post("/", data, config);
};

//PUT方式
//引数は処理に必要なデータ
//クエリが必要な場合は、paramsに設定
const usePut = (data) => {
  const config = {
    params: {
      //?id=1234の場合、id:1234
    },
  };
  return axios.put("/", data, config);
};

//PATCH方式
//引数は処理に必要なデータ
//クエリが必要な場合は、paramsに設定
const usePatch = (data) => {
  const config = {
    params: {
      //?id=1234の場合、id:1234
    },
  };
  return axios.patch("/", data, config);
};

//DELETE方式
//引数は処理に必要なデータ
//クエリが必要な場合は、paramsに設定
const useDelete = (data) => {
  const config = {
    params: {
      //?id=1234の場合、id:1234
    },
  };
  return axios.delete("/", data, config);
};

export { useGet, usePost, usePut, usePatch, useDelete };
