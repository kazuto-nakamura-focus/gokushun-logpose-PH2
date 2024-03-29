import Cookies from "js-cookie";

export class AuthCookies {
    constructor() { }
    set(name, data, days) {
        Cookies.remove(name);
        Cookies.set(name, data, { path: '/', expires: days });
    }
    get(name) {
        return Cookies.get(name);
    }
    remove(name) {
        Cookies.remove(name);
    }
}