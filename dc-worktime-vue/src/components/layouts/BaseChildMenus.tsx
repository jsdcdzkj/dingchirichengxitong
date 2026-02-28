import { Menu } from "@element-plus/icons-vue";
import { ElIcon, ElMenu, ElMenuItem, ElSubMenu } from "element-plus";
import { defineComponent, h } from "vue";
import { array, bool, string } from "vue-types";
import { Icon } from "../icon/icon";

export default defineComponent({
	props: {
		collapse: bool().def(false),
		rawList: array().def([]),
		defaultActive: string().def(),
		defaultOpend: array<string>().def()
	},
	setup(props) {
		function getCMenuStruct(menusList: any[]) {
			return menusList.map((item: any) => {
				if (item.children && item.children.length > 0) {
					const menuItems = getCMenuStruct(item.children);
					return h(
						ElSubMenu,
						{ index: item.path },
						{
							title: () => h("span", item.name),
							default: () => menuItems
						}
					);
				} else {
					return h(
						ElMenuItem,
						{ index: item.path },
						{
							title: () => (
								<span class="el-menu-item-span m-l-8px">
									{item.title}
								</span>
							),
							default: () => (
								<Icon
									name={item.icon}
									size={{
										width: "24px",
										height: "24px"
									}}
								></Icon>
							)
						}
					);
				}
			});
		}

		return () => {
			return (
				<ElMenu
					router
					defaultActive={props.defaultActive}
					class="el-menu-vertical-demo p-t-24px p-b-24px"
					mode="vertical"
					collapse={props.collapse}
					uniqueOpened={true}
					defaultOpeneds={props.defaultOpend}
					collapseTransition={false}
				>
					{getCMenuStruct(props.rawList)}
				</ElMenu>
			);
		};
	}
});
