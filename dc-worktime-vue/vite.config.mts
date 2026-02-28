import { defineConfig } from "vite";
import Vue from "@vitejs/plugin-vue";
import VueRouter from "unplugin-vue-router/vite";
import Unocss from "unocss/vite";
import VueJsx from "@vitejs/plugin-vue-jsx";
import path from "node:path";
import Components from "unplugin-vue-components/vite";

export default defineConfig({
	plugins: [
		VueRouter({
			extensions: [".vue", ".md"],
			dts: "src/typed-router.d.ts",
			// 不好包含dialog
			exclude: ["src/pages/modules/**/dialog/**"],
			routesFolder: "src/pages/modules"
		}),
		Components({
			// allow auto load markdown components under `./src/components/`
			extensions: ["vue", "md"],
			// allow auto import and register components used in markdown
			include: [/\.vue$/, /\.vue\?vue/, /\.md$/],
			resolvers: [
				// ElementPlusResolver({
				// 	importStyle: "sass"
				// })
			],
			dts: "src/components.d.ts"
		}),
		Vue(),
		VueJsx(),
		Unocss()
	],

	resolve: {
		alias: {
			"@": path.resolve(__dirname, "src"),
			"@@": path.resolve(__dirname, "packages")
		},
		extensions: [".tsx", ".ts", ".jsx", ".js", ".css"]
	},
	css: {
		preprocessorOptions: {
			scss: {
				additionalData: `@use "@/styles/element/index.scss" as *;`,
				api: "modern-compiler"
			}
		}
	},
	build: {},
	ssr: {
		noExternal: ["element-plus"]
	},
	server: {
		port: 5173,
		host: "0.0.0.0",
		proxy: {
			"/api": {
				target: "http://172.168.10.59:8088/",
				changeOrigin: true,
				rewrite: (path) => path.replace(/^\/api/, "")
			},
			"/picture": {
				target: "http://172.168.95.59/",
				changeOrigin: true
				// rewrite: (path) => path.replace(/^\/api/, "")
			},
			"/file": {
				target: "http://172.168.95.59/",
				changeOrigin: true
				// rewrite: (path) => path.replace(/^\/api/, "")
			}
		}
	}
});
