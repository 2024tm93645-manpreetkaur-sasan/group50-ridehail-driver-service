import app from "./app.js";
import { seedDatabase } from "./scripts/seed.js";
import { waitForDB } from "./scripts/waitForDB.js";
import { logger } from "./middleware/logging.js";

const port = process.env.PORT || 9084;

(async () => {
  try {
    await waitForDB();
    await seedDatabase();

    app.listen(port, () => {
      logger.info({ event: "server_start", port }, "driver-service listening");
    });
  } catch (e) {
    console.error("Failed to start service:", e);
    process.exit(1);
  }
})();
