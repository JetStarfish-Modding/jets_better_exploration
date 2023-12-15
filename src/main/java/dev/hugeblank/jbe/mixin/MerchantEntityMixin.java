package dev.hugeblank.jbe.mixin;

import net.minecraft.entity.passive.MerchantEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.item.Items;
import net.minecraft.resource.featuretoggle.FeatureFlags;
import net.minecraft.village.TradeOffer;
import net.minecraft.village.TradeOfferList;
import net.minecraft.village.TradeOffers;
import net.minecraft.village.VillagerProfession;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.ArrayList;

@Mixin(MerchantEntity.class)
public abstract class MerchantEntityMixin {

    @SuppressWarnings("InvalidInjectorMethodSignature")
    @Inject(at = @At(value = "INVOKE_ASSIGN", target = "Lcom/google/common/collect/Lists;newArrayList([Ljava/lang/Object;)Ljava/util/ArrayList;"), method = "fillRecipesFromPool(Lnet/minecraft/village/TradeOfferList;[Lnet/minecraft/village/TradeOffers$Factory;I)V", locals = LocalCapture.CAPTURE_FAILHARD)
    private void jbe$bonusBook(TradeOfferList recipeList, TradeOffers.Factory[] pool, int count, CallbackInfo ci, ArrayList<TradeOffers.Factory> arrayList) {
        MerchantEntity thiz = (MerchantEntity) (Object) this;
        if (thiz.getWorld().getEnabledFeatures().contains(FeatureFlags.TRADE_REBALANCE) && thiz instanceof VillagerEntity && ((VillagerEntity) thiz).getVillagerData().getProfession().equals(VillagerProfession.LIBRARIAN)) {
            for (TradeOffers.Factory factory : arrayList) {
                if (factory instanceof TradeOffers.EnchantBookFactory) {
                    arrayList.add(factory); // Increase the chance to get a book trade
                }
            }
        }
    }


    @Inject(at = @At(value = "TAIL"), method = "fillRecipesFromPool(Lnet/minecraft/village/TradeOfferList;[Lnet/minecraft/village/TradeOffers$Factory;I)V", locals = LocalCapture.CAPTURE_FAILHARD)
    private void jbe$deduplicate(TradeOfferList recipeList, TradeOffers.Factory[] pool, int count, CallbackInfo ci, ArrayList<TradeOffers.Factory> arrayList, int i) {
        boolean hasBook = false;
        for (TradeOffer tradeOffer : recipeList) {
            if (tradeOffer.getSellItem().getItem().equals(Items.ENCHANTED_BOOK)) {
                if (hasBook) {
                    recipeList.remove(tradeOffer); // If there's already a book trade, remove this one
                    MerchantEntity thiz = (MerchantEntity) (Object) this;
                    if (!arrayList.isEmpty()) { // And attempt to pull for a different one
                        TradeOffer newOffer = arrayList.remove(thiz.getRandom().nextInt(arrayList.size())).create(thiz, thiz.getRandom());
                        if (newOffer != null) {
                            recipeList.add(newOffer);
                        }
                    }
                    break;
                }
                hasBook = true;
            }
        }
    }
}
