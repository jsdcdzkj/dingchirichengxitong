import { request } from "../instance";

export const reportKPI = (data: any) => request.post("/sysTeamUser/reportKPI", data);

