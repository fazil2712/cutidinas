# Graph Report - .  (2026-07-22)

## Corpus Check
- Corpus is ~21,133 words - fits in a single context window. You may not need a graph.

## Summary
- 87 nodes · 68 edges · 28 communities (7 shown, 21 thin omitted)
- Extraction: 88% EXTRACTED · 12% INFERRED · 0% AMBIGUOUS · INFERRED: 8 edges (avg confidence: 0.84)
- Token cost: 0 input · 0 output

## Community Hubs (Navigation)
- [[_COMMUNITY_Custom User Details|Custom User Details]]
- [[_COMMUNITY_Admin Controller|Admin Controller]]
- [[_COMMUNITY_Application Bootstrap|Application Bootstrap]]
- [[_COMMUNITY_Domain Models|Domain Models]]
- [[_COMMUNITY_User Services|User Services]]
- [[_COMMUNITY_Thymeleaf Templates|Thymeleaf Templates]]
- [[_COMMUNITY_Security Configuration|Security Configuration]]
- [[_COMMUNITY_Login Controller|Login Controller]]
- [[_COMMUNITY_Data Access|Data Access]]
- [[_COMMUNITY_Application Tests|Application Tests]]
- [[_COMMUNITY_Cuti Entity|Cuti Entity]]
- [[_COMMUNITY_Helper Scripts|Helper Scripts]]
- [[_COMMUNITY_Cuti Repository|Cuti Repository]]
- [[_COMMUNITY_Pengajuan Cuti Entity|Pengajuan Cuti Entity]]
- [[_COMMUNITY_User Entity|User Entity]]
- [[_COMMUNITY_Pengajuan Cuti Repository|Pengajuan Cuti Repository]]
- [[_COMMUNITY_Maven Wrapper|Maven Wrapper]]
- [[_COMMUNITY_Main Application|Main Application]]
- [[_COMMUNITY_Security Config Class|Security Config Class]]
- [[_COMMUNITY_Login Controller Class|Login Controller Class]]
- [[_COMMUNITY_Tests Class|Tests Class]]
- [[_COMMUNITY_Graphify Rule|Graphify Rule]]
- [[_COMMUNITY_Graphify Workflow|Graphify Workflow]]
- [[_COMMUNITY_Index Template|Index Template]]
- [[_COMMUNITY_Login Template|Login Template]]
- [[_COMMUNITY_Company Logo|Company Logo]]

## God Nodes (most connected - your core abstractions)
1. `CustomUserDetails` - 15 edges
2. `AdminController` - 6 edges
3. `DataInitializer` - 3 edges
4. `SecurityConfig` - 3 edges
5. `LoginController` - 3 edges
6. `CustomUserDetailsService` - 3 edges
7. `Cuti` - 3 edges
8. `User` - 3 edges
9. `Layout Template` - 3 edges
10. `CutidinasApplication` - 2 edges

## Surprising Connections (you probably didn't know these)
- `Sidebar Fragment` --semantically_similar_to--> `Sidebar Fragment (Ref)`  [INFERRED] [semantically similar]
  src/main/resources/templates/fragments/sidebar.html → reference/sidebar-fragment.html
- `RecordToolUse Shell Script` --semantically_similar_to--> `RecordToolUse PowerShell Script`  [INFERRED] [semantically similar]
  .github/modernize/java-upgrade/hooks/scripts/recordToolUse.sh → .github/modernize/java-upgrade/hooks/scripts/recordToolUse.ps1
- `AdminController` --semantically_similar_to--> `DataInitializer`  [INFERRED] [semantically similar]
  src/main/java/com/ppip/cutidinas/controller/AdminController.java → src/main/java/com/ppip/cutidinas/bootstrap/DataInitializer.java
- `DataInitializer` --references--> `User`  [EXTRACTED]
  src/main/java/com/ppip/cutidinas/bootstrap/DataInitializer.java → src/main/java/com/ppip/cutidinas/model/User.java
- `AdminController` --references--> `User`  [EXTRACTED]
  src/main/java/com/ppip/cutidinas/controller/AdminController.java → src/main/java/com/ppip/cutidinas/model/User.java

## Hyperedges (group relationships)
- **JPA Domain Entities** — cuti_model, pengajuancuti_model, user_model [INFERRED 0.95]
- **Thymeleaf Layout System** — admin_edit_template, admin_users_template, layout_template, sidebar_fragment [INFERRED 0.95]

## Communities (28 total, 21 thin omitted)

### Community 2 - "Application Bootstrap"
Cohesion: 0.33
Nodes (3): DataInitializer, CommandLineRunner, CutidinasApplication

### Community 3 - "Domain Models"
Cohesion: 0.33
Nodes (7): AdminController, Cuti, CutiRepository, DataInitializer, PengajuanCuti, PengajuanCutiRepository, User

### Community 4 - "User Services"
Cohesion: 0.33
Nodes (3): UserRepository, CustomUserDetailsService, UserDetailsService

### Community 5 - "Thymeleaf Templates"
Cohesion: 0.40
Nodes (5): Admin Edit Template, Admin Users Template, Layout Template, Sidebar Fragment, Sidebar Fragment (Ref)

### Community 8 - "Data Access"
Cohesion: 0.67
Nodes (3): CustomUserDetails, CustomUserDetailsService, UserRepository

## Knowledge Gaps
- **23 isolated node(s):** `Cuti`, `PengajuanCuti`, `User`, `CutiRepository`, `PengajuanCutiRepository` (+18 more)
  These have ≤1 connection - possible missing edges or undocumented components.
- **21 thin communities (<3 nodes) omitted from report** — run `graphify query` to explore isolated nodes.

## Suggested Questions
_Questions this graph is uniquely positioned to answer:_

- **Why does `CustomUserDetails` connect `Custom User Details` to `Admin Controller`?**
  _High betweenness centrality (0.090) - this node is a cross-community bridge._
- **What connects `Cuti`, `PengajuanCuti`, `User` to the rest of the system?**
  _23 weakly-connected nodes found - possible documentation gaps or missing edges._