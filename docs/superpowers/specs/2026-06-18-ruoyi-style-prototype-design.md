# RuoYi-Style Prototype Redesign

## Context

The project contains a Spring Boot backend and a single-file clickable prototype at `prototype/index.html`. The prototype already covers the core book management workflows: login, dashboard, book records, collection copies, borrowing, returns, fines, users, roles, statistics, and reader self-service.

The requested change is to adjust the prototype UI style to reference the RuoYi frontend style. The selected direction is a closer management-console structure, not a pixel-perfect clone.

## Goals

- Keep the existing book management business scope and clickable interactions.
- Rework the UI into a RuoYi-like admin console: dark sidebar, top toolbar, breadcrumb, tab strip, query forms, tool button rows, data tables, and pagination.
- Make list pages feel like real backend management screens while still remaining a static clickable prototype.
- Use a restrained blue/white/gray palette similar to RuoYi and avoid the current library-themed green/gold visual style.
- Verify the prototype by simulating key clicks in a browser after implementation.

## Non-Goals

- Do not integrate with the backend API.
- Do not split the prototype into a frontend build system.
- Do not implement real pagination, authentication, or persistence.
- Do not exactly copy RuoYi source code or assets.

## Design

### Login

The login screen will use a management-system login layout: blue gradient background, centered or right-side white login panel, system title, username and password fields, login button, and quick role buttons for super admin, librarian, and reader.

### Application Shell

After login, the prototype will show a RuoYi-like shell:

- Left dark sidebar with grouped modules.
- Top toolbar with menu toggle, breadcrumb, global search, current role/user, and logout.
- Tag-view strip showing the current page as an active tab.
- Main content area with light gray background and white panels.

The existing role switcher will remain available in the sidebar or top user area because it is useful for prototype review.

### Page Patterns

Management pages will follow a consistent structure:

- Query form with common filters.
- Action toolbar with primary and secondary operations.
- Data table with status tags and row-level actions.
- Pagination footer for prototype realism.

This pattern applies to book records, collection copies, borrow records, return handling, fines, users, roles, and statistics where relevant.

### Business Interactions

Existing static interactions will be preserved:

- Login and quick login.
- Role switching.
- Sidebar navigation.
- Global search filtering.
- Book detail drawer.
- Add book modal.
- Borrow confirmation modal.
- Return confirmation modal.
- Toast feedback.
- Reader tabs for borrowing, returned, and overdue records.

### Responsive Behavior

The prototype should remain usable on narrower screens:

- Sidebar stacks or becomes non-sticky.
- Query forms and tables should not overlap.
- Buttons and status text should fit their containers.

## Testing Plan

After implementation, use browser automation to open `prototype/index.html` and simulate:

- Login.
- Navigate to dashboard, book records, copies, borrow, return, fines, users, roles, statistics, and reader pages.
- Open book detail drawer.
- Open and close add book modal.
- Complete borrow confirmation.
- Complete return confirmation.
- Switch roles and verify menu/content updates.
- Search for a book and verify filtered table results.

Visual checks should confirm that no core UI element is blank, visibly overlapped, or broken in the main desktop viewport.
