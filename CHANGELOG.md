# Changelog

All notable changes to the Oris backend (Spring Boot).

## [2026-03-08]

### Added
- Standardized PDF header (Oris logo, company name, generation timestamp) for all Excel→Gotenberg reports
- Geozone report PDF redesign with proper layout, zebra striping, and fixed column widths
- PDF loading overlay support (frontend coordination)

### Changed
- All 9 Excel-based report export methods now use `addExcelReportHeader()` helper
- Report controllers pass `firmName` parameter to all export methods

### Fixed
- Speeding report PDF 500/502 errors resolved
- Monthly working hours report (`ippmh`) — fixed undefined path parameters
- Failed notification logs now save `firmId` instead of `userId`, added email field
- Galebmongo health check URL corrected

## [2026-03-07]

### Added
- `AdminHealthController` with `/api/admin/health` endpoint for monitoring
- Cross-server health check endpoints

## Pre-2026-03-07

Production-stable Spring Boot backend with report generation, vehicle management, user authentication, notification system, and FMS integration.
