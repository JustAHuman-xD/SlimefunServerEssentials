package me.justahuman.slimefun_server_essentials.api.display;

import me.justahuman.slimefun_server_essentials.api.OffsetBuilder;
import me.justahuman.slimefun_server_essentials.util.Utils;

import static me.justahuman.slimefun_server_essentials.implementation.core.DefaultComponentTypes.*;

public class GridDisplayBuilder extends AbstractDisplayBuilder<GridDisplayBuilder> {
    public GridDisplayBuilder(int inputRows, int inputColumns, int outputRows, int outputColumns) {
        OffsetBuilder offsets = new OffsetBuilder(
                PADDING, PADDING,
                PADDING + ENERGY.width() + PADDING + (SLOT.size() * inputRows) + PADDING + ARROW_LEFT.width() + PADDING + (SLOT.size() * outputRows) + PADDING,
                PADDING + Utils.max(SLOT.size() * inputColumns, SLOT.size() * outputColumns) + PADDING
        );

        energy(energy -> energy.x(offsets).centeredY(offsets));
        offsets.x().addEnergy();

        offsets.y().set((offsets.y().max() - SLOT.size() * inputColumns) / 2);
        for (int r = 0; r < inputRows; r++) {
            for (int c = 0; c < inputColumns; c++) {
                int index = r + 1 + c * inputRows;
                slot(slot -> slot.pos(offsets).index(index));
                offsets.y().addSlot(false);
            }
            offsets.y().subtract(SLOT.size() * inputColumns);
            offsets.x().addSlot(false);
        }
        offsets.x().addPadding();

        fillingArrowRight(arrow -> arrow.x(offsets).centeredY(offsets));
        offsets.x().addArrow();

        offsets.y().set((offsets.y().max() - SLOT.size() * outputColumns) / 2);
        for (int r = 0; r < outputRows; r++) {
            for (int c = 0; c < outputColumns; c++) {
                int index = r + 1 + c * outputRows;
                slot(slot -> slot.pos(offsets).index(index).output());
                offsets.y().addSlot(false);
            }
            offsets.y().subtract(SLOT.size() * outputColumns);
            offsets.x().addSlot(false);
        }

        this.width = offsets.x().max();
        this.height = offsets.y().max();
    }
}
