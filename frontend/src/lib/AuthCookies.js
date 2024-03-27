import Cookies from "js-cookie";

export class AuthCookies {
    constructor() { }
    set(name, data, days) {
        Cookies.set(name, data, days);
    }
    get(name) {
        return Cookies.get(name);
    }
}