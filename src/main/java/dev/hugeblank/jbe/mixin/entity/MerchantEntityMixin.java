package dev.hugeblank.jbe.mixin.entity;

import dev.hugeblank.jbe.mixin.village.TradeOfferAccessor;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.MerchantEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.item.Items;
import net.minecraft.village.TradeOffer;
import net.minecraft.village.TradeOfferList;
import net.minecraft.village.TradeOffers;
import net.minecraft.village.VillagerProfession;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.ArrayList;

@Mixin(MerchantEntity.class)
public abstract class MerchantEntityMixin extends PassiveEntity {

    protected MerchantEntityMixin(EntityType<? extends PassiveEntity> entityType, World world) {
        super(entityType, world);
    }

    @SuppressWarnings("InvalidInjectorMethodSignature")
    @Inject(at = @At(value = "INVOKE_ASSIGN", target = "Lcom/google/common/collect/Lists;newArrayList([Ljava/lang/Object;)Ljava/util/ArrayList;"), method = "fillRecipesFromPool(Lnet/minecraft/village/TradeOfferList;[Lnet/minecraft/village/TradeOffers$Factory;I)V", locals = LocalCapture.CAPTURE_FAILHARD)
    private void jbe$bonusBook(TradeOfferList recipeList, TradeOffers.Factory[] pool, int count, CallbackInfo ci, ArrayList<TradeOffers.Factory> arrayList) {
        //noinspection ConstantValue
        if ((Object) this instanceof VillagerEntity villager && villager.getVillagerData().getProfession() == VillagerProfession.LIBRARIAN) {
            int experience = 0;
            TradeOffers.Factory target = null;
            for (TradeOffers.Factory factory : arrayList) {
                if (factory instanceof TradeOffers.TypedWrapperFactory) {
                    TradeOffer offer = factory.create(this, this.getRandom());
                    if (offer.getSellItem().isOf(Items.ENCHANTED_BOOK)) {
                        target = factory;
                        experience = ((TradeOfferAccessor) offer).getMerchantExperience();
                    }
                }
            }
            if (target != null && this.getRandom().nextInt(3) > 0) {
                arrayList.remove(target);
                arrayList.add(new TradeOffers.EnchantBookFactory(experience)); // Increase the chance to get a book trade
            }
        }
    }
}
