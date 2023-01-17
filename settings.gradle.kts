
rootProject.name = "mcphysics"
include("core")
include("extensions")
include("extensions:tap")
findProject(":extensions:tap")?.name = "tap"
include("plugin")
