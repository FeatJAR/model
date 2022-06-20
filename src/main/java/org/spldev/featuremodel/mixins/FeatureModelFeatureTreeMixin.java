package org.spldev.featuremodel.mixins;

import org.spldev.featuremodel.Feature;
import org.spldev.featuremodel.FeatureModel;
import org.spldev.featuremodel.FeatureTree;
import org.spldev.featuremodel.Identifier;
import org.spldev.util.tree.Trees;

import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public interface FeatureModelFeatureTreeMixin {
    FeatureTree getFeatureTree();

    default Set<Feature> getFeatures() {
        return Trees.parallelStream(getFeatureTree()).map(FeatureTree::getFeature).collect(Collectors.toSet());
    }

    default int getNumberOfFeatures() {
        return getFeatures().size();
    }

    default Feature getRootFeature() {
        return getFeatureTree().getRoot().getFeature();
    }

    default Optional<Feature> getFeature(Identifier<?> identifier) {
        Objects.requireNonNull(identifier);
        return getFeatures().stream().filter(feature -> feature.getIdentifier().equals(identifier)).findFirst();
    }

    default boolean hasFeature(Identifier<?> identifier) {
        return getFeature(identifier).isPresent();
    }

    default boolean hasFeature(Feature feature) {
        return hasFeature(feature.getIdentifier());
    }

    interface Mutator extends MutableMixin.Mutator<FeatureModel> {
        default void addFeatureBelow(Feature newFeature, Feature parentFeature, int index) {
            Objects.requireNonNull(newFeature);
            Objects.requireNonNull(parentFeature);
            if (getMutable().hasFeature(newFeature) || !getMutable().hasFeature(parentFeature)) {
                throw new IllegalArgumentException();
            }
            parentFeature.getFeatureTree().addChild(index, newFeature.getFeatureTree());
        }

        default void addFeatureBelow(Feature newFeature, Feature parentFeature) {
            Objects.requireNonNull(newFeature);
            Objects.requireNonNull(parentFeature);
            addFeatureBelow(newFeature, parentFeature, parentFeature.getFeatureTree().getNumberOfChildren());
        }

        default void addFeatureNextTo(Feature newFeature, Feature siblingFeature) {
            Objects.requireNonNull(newFeature);
            Objects.requireNonNull(siblingFeature);
            if (!siblingFeature.getFeatureTree().hasParent() || !getMutable().hasFeature(siblingFeature)) {
                throw new IllegalArgumentException();
            }
            addFeatureBelow(newFeature,
                    siblingFeature.getFeatureTree().getParent().get().getFeature(),
                    siblingFeature.getFeatureTree().getIndex().get() + 1);
        }

        default Feature createFeatureBelow(Feature parentFeature, int index) {
            Feature newFeature = new Feature(getMutable());
            addFeatureBelow(newFeature, parentFeature, index);
            return newFeature;
        }

        default Feature createFeatureBelow(Feature parentFeature) {
            Feature newFeature = new Feature(getMutable());
            addFeatureBelow(newFeature, parentFeature);
            return newFeature;
        }

        default Feature createFeatureNextTo(Feature siblingFeature) {
            Feature newFeature = new Feature(getMutable());
            addFeatureNextTo(newFeature, siblingFeature);
            return newFeature;
        }

        default void removeFeature(Feature feature) {
            Objects.requireNonNull(feature);
            if (feature.equals(getMutable().getRootFeature()) || !getMutable().hasFeature(feature)) {
                throw new IllegalArgumentException();
            }

            final FeatureTree parentFeatureTree = feature.getFeatureTree().getParent().get();

            if (parentFeatureTree.getNumberOfChildren() == 1) {
                if (feature.getFeatureTree().isAnd()) {
                    parentFeatureTree.setAnd();
                } else if (feature.getFeatureTree().isAlternative()) {
                    parentFeatureTree.setAlternative();
                } else {
                    parentFeatureTree.setOr();
                }
            }

            final int index = feature.getFeatureTree().getIndex().get();
            while (feature.getFeatureTree().hasChildren()) {
                parentFeatureTree.addChild(
                        index,
                        feature.getFeatureTree().removeChild(feature.getFeatureTree().getNumberOfChildren() - 1));
            }

            parentFeatureTree.removeChild(feature.getFeatureTree());
        }
    }
}