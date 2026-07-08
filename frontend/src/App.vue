<template>
  <div id="app">
    <router-view />
  </div>
</template>

<script>

export default {
  name: 'App',

}
</script>

<style>
@import './css/index.css';
@import "./css/font/iconfont.css";

/* ==================== CSS Design System ==================== */
:root {
  /* Primary palette - Teal */
  --primary: #28b2d4;
  --primary-dark: #1a8da8;
  --primary-light: #5ccce6;
  --primary-bg: #e8f8fc;
  --primary-gradient: linear-gradient(135deg, #28b2d4 0%, #1a8da8 100%);

  /* Accent colors */
  --accent-warm: #f7ad25;
  --accent-purple: #7330a0;
  --accent-green: #27cc8d;
  --accent-pink: #b620a2;
  --accent-red: #e74c3c;
  --accent-orange: #e67e22;

  /* Neutrals */
  --bg-page: #f0f4f8;
  --bg-card: #ffffff;
  --text-primary: #2c3e50;
  --text-secondary: #606c78;
  --text-muted: #95a5a6;
  --border-color: #e8ecf0;
  --border-light: #f0f2f5;

  /* Shadows */
  --shadow-sm: 0 1px 3px rgba(0,0,0,0.06), 0 1px 2px rgba(0,0,0,0.04);
  --shadow-md: 0 4px 12px rgba(0,0,0,0.08), 0 1px 4px rgba(0,0,0,0.04);
  --shadow-lg: 0 12px 32px rgba(0,0,0,0.10), 0 4px 8px rgba(0,0,0,0.05);
  --shadow-xl: 0 20px 48px rgba(0,0,0,0.12), 0 8px 16px rgba(0,0,0,0.06);
  --shadow-card-hover: 0 8px 24px rgba(40,178,212,0.12), 0 2px 8px rgba(0,0,0,0.06);

  /* Radius */
  --radius-sm: 6px;
  --radius-md: 10px;
  --radius-lg: 16px;
  --radius-xl: 20px;

  /* Transitions */
  --transition-fast: 0.15s ease;
  --transition-base: 0.25s ease;
  --transition-slow: 0.4s cubic-bezier(0.4, 0, 0.2, 1);
}

html,
body {
  background-color: var(--bg-page);
  scroll-behavior: smooth;
}

/* ==================== Header ==================== */
.bg-header {
  background: var(--primary-gradient);
  display: flex;
  height: 60px;
  align-items: center;
  justify-content: space-between;
  padding: 0 24px;
  box-shadow: 0 2px 12px rgba(40,178,212,0.25);
  color: white;
  position: sticky;
  top: 0;
  z-index: 100;
}

/* ==================== Utility Classes ==================== */
.pointer {
  cursor: pointer;
}

/* Card - enhanced */
.card {
  background: var(--bg-card);
  border-radius: var(--radius-md);
  padding: 24px;
  box-shadow: var(--shadow-sm);
  border: 1px solid var(--border-light);
  transition: box-shadow var(--transition-base);
  height: 100%;
}

.card:hover {
  box-shadow: var(--shadow-md);
}

/* Card with accent top border */
.card-accent {
  border-top: 3px solid var(--primary);
}

/* Glass card effect */
.card-glass {
  background: rgba(255,255,255,0.85);
  backdrop-filter: blur(12px);
  -webkit-backdrop-filter: blur(12px);
  border: 1px solid rgba(255,255,255,0.3);
}

/* ==================== Section Headers ==================== */
.section-header {
  display: flex;
  align-items: center;
  margin-bottom: 20px;
  padding-bottom: 12px;
  border-bottom: 1px solid var(--border-color);
}

.section-header .title {
  font-size: 18px;
  font-weight: 700;
  color: var(--text-primary);
  position: relative;
  padding-left: 14px;
}

.section-header .title::before {
  content: '';
  position: absolute;
  left: 0;
  top: 2px;
  bottom: 2px;
  width: 4px;
  background: var(--primary-gradient);
  border-radius: 2px;
}

/* ==================== Item Header (legacy support) ==================== */
.item-header {
  border-left: 4px solid var(--primary);
  padding-left: 12px;
  font-weight: 700;
  font-size: 17px;
  color: var(--text-primary);
  margin-bottom: 16px;
}

/* ==================== Margin Utilities ==================== */
.margin-top-xs { margin-top: 8px; }
.margin-top-sm { margin-top: 12px; }
.margin-top-lg { margin-top: 20px; }
.margin-top-xl { margin-top: 32px; }
.margin-bottom-xs { margin-bottom: 8px; }
.margin-bottom-sm { margin-bottom: 12px; }
.margin-bottom-lg { margin-bottom: 20px; }
.margin-bottom-xl { margin-bottom: 32px; }

/* ==================== Status Tags ==================== */
.status-tag {
  display: inline-block;
  padding: 4px 12px;
  border-radius: 20px;
  font-size: 12px;
  font-weight: 600;
  letter-spacing: 0.3px;
}

.status-success { background: #e8f8f0; color: #27ae60; }
.status-warning { background: #fef5e7; color: #f39c12; }
.status-danger { background: #fdedec; color: #e74c3c; }
.status-info { background: var(--primary-bg); color: var(--primary-dark); }
.status-purple { background: #f3eef8; color: #7330a0; }

/* ==================== Animated Elements ==================== */
.fade-in {
  animation: fadeInUp 0.5s ease forwards;
  opacity: 0;
}

@keyframes fadeInUp {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

@keyframes pulse {
  0%, 100% { transform: scale(1); }
  50% { transform: scale(1.05); }
}

/* ==================== Scrollbar Enhancement ==================== */
::-webkit-scrollbar {
  width: 6px;
  height: 6px;
}

::-webkit-scrollbar-track {
  background: transparent;
}

::-webkit-scrollbar-thumb {
  background: #c8d6e5;
  border-radius: 3px;
}

::-webkit-scrollbar-thumb:hover {
  background: #a0b3c6;
}

/* ==================== Element UI Overrides ==================== */

/* Softer primary button */
.el-button--primary {
  background: var(--primary-gradient) !important;
  border-color: transparent !important;
  box-shadow: 0 2px 8px rgba(40,178,212,0.3);
  transition: all var(--transition-base);
}

.el-button--primary:hover {
  box-shadow: 0 4px 16px rgba(40,178,212,0.45);
  transform: translateY(-1px);
}

.el-button--primary:active {
  transform: translateY(0);
}

/* Better card header */
.el-card__header {
  border-bottom: 1px solid var(--border-color) !important;
  padding: 16px 20px !important;
  font-weight: 600;
  color: var(--text-primary);
  background: #fafbfc;
  border-radius: var(--radius-md) var(--radius-md) 0 0 !important;
}

.el-card {
  border: 1px solid var(--border-color) !important;
  border-radius: var(--radius-md) !important;
  box-shadow: var(--shadow-sm) !important;
  overflow: hidden;
}

.el-card:hover {
  box-shadow: var(--shadow-md) !important;
}

/* Better dialog */
.el-dialog {
  border-radius: var(--radius-lg) !important;
  overflow: hidden;
  box-shadow: var(--shadow-xl) !important;
}

.el-dialog__header {
  background: #fafbfc;
  border-bottom: 1px solid var(--border-color);
  padding: 20px 24px !important;
}

.el-dialog__title {
  font-weight: 700;
  font-size: 17px;
  color: var(--text-primary);
}

.el-dialog__body {
  padding: 24px !important;
}

/* Better table */
.el-table {
  border-radius: var(--radius-sm);
  overflow: hidden;
}

.el-table th {
  background: #f8fafb !important;
  color: var(--text-primary) !important;
  font-weight: 600 !important;
  font-size: 13px;
  letter-spacing: 0.3px;
  border-bottom: 2px solid var(--border-color) !important;
}

.el-table tr:hover td {
  background: var(--primary-bg) !important;
}

/* Better pagination */
.el-pagination {
  margin-top: 16px;
}

/* Better tabs */
.el-tabs__item.is-active {
  color: var(--primary) !important;
  font-weight: 600;
}

.el-tabs__active-bar {
  background-color: var(--primary) !important;
  height: 3px !important;
}

.el-tabs__item:hover {
  color: var(--primary) !important;
}

/* Better form items */
.el-form-item__label {
  color: var(--text-secondary) !important;
  font-weight: 600 !important;
  font-size: 13px;
}

/* Better input focus */
.el-input__inner:focus {
  border-color: var(--primary) !important;
  box-shadow: 0 0 0 2px rgba(40,178,212,0.15) !important;
}

.el-textarea__inner:focus {
  border-color: var(--primary) !important;
  box-shadow: 0 0 0 2px rgba(40,178,212,0.15) !important;
}

/* Better select focus */
.el-select .el-input.is-focus .el-input__inner {
  border-color: var(--primary) !important;
}

/* Better radio */
.el-radio__input.is-checked .el-radio__inner {
  background: var(--primary) !important;
  border-color: var(--primary) !important;
}

.el-radio__input.is-checked + .el-radio__label {
  color: var(--primary) !important;
}

/* Better switch */
.el-switch.is-checked .el-switch__core {
  background: var(--primary) !important;
  border-color: var(--primary) !important;
}

/* Better menu */
.el-menu {
  border-right: none !important;
}

/* Better breadcrumb */
.el-breadcrumb {
  padding: 12px 0;
}

.el-breadcrumb__item .el-breadcrumb__inner {
  color: var(--text-muted) !important;
  font-weight: 500;
}

.el-breadcrumb__item:last-child .el-breadcrumb__inner {
  color: var(--text-primary) !important;
  font-weight: 700;
}

/* Better page header */
.el-page-header__title {
  font-weight: 700 !important;
  font-size: 18px !important;
  color: var(--text-primary) !important;
}

/* Better tag */
.el-tag {
  border-radius: 20px !important;
  font-weight: 600;
  letter-spacing: 0.3px;
}

/* Better rate */
.el-rate__icon.is-active {
  color: #f7ad25 !important;
}

/* Better empty */
.el-empty__description {
  color: var(--text-muted) !important;
}

/* Loading spinner color */
.el-loading-spinner .circular circle {
  stroke: var(--primary) !important;
}

/* ==================== NProgress Override ==================== */
#nprogress .bar {
  background: var(--primary) !important;
  height: 3px !important;
}

#nprogress .peg {
  box-shadow: 0 0 10px var(--primary), 0 0 5px var(--primary) !important;
}

/* ==================== app-container for admin pages ==================== */
.app-container {
  padding: 0;
}

/* Table body search area */
.tb-body {
  padding: 4px 0;
}

/* ==================== Responsive text ==================== */
@media (max-width: 768px) {
  body {
    font-size: 15px;
  }
}
</style>
