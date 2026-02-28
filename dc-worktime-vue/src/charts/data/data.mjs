import dayjs from "dayjs";
export const getDate = () => {
  return [
    { startDate: "2025-03-18", endDate: "2025-03-24", row: 1 },
    { startDate: "2025-03-01", endDate: "2025-03-18", row: 2 },
    { startDate: "2025-02-20", endDate: "2025-03-08", row: 3 },
    { startDate: "2025-04-10", endDate: "2025-04-25", row: 3 }
  ];
};
export const getTreeList = (data, list) => {
  for (const item of data) {
    if (item.children && item.children.length > 0 && !item.col) {
      list.push({
        ...item
      });
      getTreeList(item.children, list);
    } else {
      list.push({ ...item });
    }
  }
  return list;
};
export const coverDataLoop = (data, list, margin) => {
  margin += 1;
  for (const item of data) {
    if (item.children && item.children.length > 0) {
      list.push({
        ...item,
        node: item,
        margin,
        col: false,
        children: coverDataLoop(item.children, [], margin)
      });
    } else {
      list.push({ ...item, node: item, margin });
    }
  }
  return list;
};
const coverDataLoopNodeLen2 = (data, index) => {
  if (data.children && data.children.length > 0 && !data.col) {
    for (const element of data.children) {
      index += coverDataLoopNodeLen2(element, index);
    }
    return index;
  } else {
    return 1;
  }
};
export const coverDataLoopNodeLen = (data) => {
  let index = 0;
  for (const element of data) {
    index += coverDataLoopNodeLen2(element, 1);
  }
  return index;
};
export const coverData = (data) => {
  const list = coverDataLoop(data, [], 0);
  return list;
};
export const getChildrenIds = (data, list) => {
  if (data.children && data.children.length > 0) {
    if (data.parentId != 0) {
      list.push(data.id);
    }
    for (const item of data.children) {
      getChildrenIds(item, list);
    }
  } else {
    list.push(data.id);
  }
};
export const getNotChildrenIds = (data, list) => {
  if (data.children && data.children.length > 0) {
    list.push(data.id);
    for (const item of data.children) {
      getChildrenIds(item, list);
    }
  } else {
    list.push(data.id);
  }
};
export const computedDate = (dataList) => {
  const toDay = dayjs();
  return dataList.map((item, index) => {
    if (item.startDate && item.endDate) {
      const startDay = dayjs(item.startDate).diff(toDay, "day");
      const endDay = dayjs(item.endDate).diff(toDay, "day");
      if (startDay == 0 && endDay == 0) {
        return {
          ...item,
          row: index,
          startCol: 0,
          endCol: 1
        };
      }
      return {
        ...item,
        row: index,
        startCol: startDay < 0 ? startDay : startDay + 1,
        endCol: endDay > 0 ? endDay + 2 : endDay + 1
      };
    }
    return null;
  });
};
