package org.apache.maven.doxia.module.rtf;

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

/**
 * MonospaceItalic
 */
class MonospaceItalic
    extends FontMetrics
{
    static final CharMetrics[] METRICS = {new CharMetrics( 600, 0, 0, 0, 0, 0 ),
        new CharMetrics( 600, 0, 0, 0, 0, 0 ), new CharMetrics( 600, 0, 0, 0, 0, 0 ),
        new CharMetrics( 600, 0, 0, 0, 0, 0 ), new CharMetrics( 600, 0, 0, 0, 0, 0 ),
        new CharMetrics( 600, 0, 0, 0, 0, 0 ), new CharMetrics( 600, 0, 0, 0, 0, 0 ),
        new CharMetrics( 600, 0, 0, 0, 0, 0 ), new CharMetrics( 600, 0, 0, 0, 0, 0 ),
        new CharMetrics( 600, 0, 0, 0, 0, 0 ), new CharMetrics( 600, 0, 0, 0, 0, 0 ),
        new CharMetrics( 600, 0, 0, 0, 0, 0 ), new CharMetrics( 600, 0, 0, 0, 0, 0 ),
        new CharMetrics( 600, 0, 0, 0, 0, 0 ), new CharMetrics( 600, 0, 0, 0, 0, 0 ),
        new CharMetrics( 600, 0, 0, 0, 0, 0 ), new CharMetrics( 600, 0, 0, 0, 0, 0 ),
        new CharMetrics( 600, 0, 0, 0, 0, 0 ), new CharMetrics( 600, 0, 0, 0, 0, 0 ),
        new CharMetrics( 600, 0, 0, 0, 0, 0 ), new CharMetrics( 600, 0, 0, 0, 0, 0 ),
        new CharMetrics( 600, 0, 0, 0, 0, 0 ), new CharMetrics( 600, 0, 0, 0, 0, 0 ),
        new CharMetrics( 600, 0, 0, 0, 0, 0 ), new CharMetrics( 600, 0, 0, 0, 0, 0 ),
        new CharMetrics( 600, 0, 0, 0, 0, 0 ), new CharMetrics( 600, 0, 0, 0, 0, 0 ),
        new CharMetrics( 600, 0, 0, 0, 0, 0 ), new CharMetrics( 600, 0, 0, 0, 0, 0 ),
        new CharMetrics( 600, 0, 0, 0, 0, 0 ), new CharMetrics( 600, 0, 0, 0, 0, 0 ),
        new CharMetrics( 600, 0, 0, 0, 0, 0 ), new CharMetrics( 600, 0, 0, 0, 0, 0 ),
        new CharMetrics( 600, 0, 243, -15, 464, 572 ), new CharMetrics( 600, 0, 273, 328, 532, 562 ),
        new CharMetrics( 600, 0, 133, -32, 596, 639 ), new CharMetrics( 600, 0, 108, -126, 596, 662 ),
        new CharMetrics( 600, 0, 134, -15, 599, 622 ), new CharMetrics( 600, 0, 87, -15, 580, 543 ),
        new CharMetrics( 600, 0, 283, 328, 495, 562 ), new CharMetrics( 600, 0, 313, -108, 572, 622 ),
        new CharMetrics( 600, 0, 137, -108, 396, 622 ), new CharMetrics( 600, 0, 212, 257, 580, 607 ),
        new CharMetrics( 600, 0, 129, 44, 580, 470 ), new CharMetrics( 600, 0, 157, -112, 370, 122 ),
        new CharMetrics( 600, 0, 152, 231, 558, 285 ), new CharMetrics( 600, 0, 238, -15, 382, 109 ),
        new CharMetrics( 600, 0, 112, -80, 604, 629 ), new CharMetrics( 600, 0, 154, -15, 575, 622 ),
        new CharMetrics( 600, 0, 98, 0, 515, 622 ), new CharMetrics( 600, 0, 70, 0, 568, 622 ),
        new CharMetrics( 600, 0, 82, -15, 538, 622 ), new CharMetrics( 600, 0, 108, 0, 541, 622 ),
        new CharMetrics( 600, 0, 99, -15, 589, 607 ), new CharMetrics( 600, 0, 155, -15, 629, 622 ),
        new CharMetrics( 600, 0, 182, 0, 612, 607 ), new CharMetrics( 600, 0, 132, -15, 588, 622 ),
        new CharMetrics( 600, 0, 93, -15, 574, 622 ), new CharMetrics( 600, 0, 238, -15, 441, 385 ),
        new CharMetrics( 600, 0, 157, -112, 441, 385 ), new CharMetrics( 600, 0, 96, 42, 610, 472 ),
        new CharMetrics( 600, 0, 109, 138, 600, 376 ), new CharMetrics( 600, 0, 85, 42, 599, 472 ),
        new CharMetrics( 600, 0, 222, -15, 583, 572 ), new CharMetrics( 600, 0, 127, -15, 582, 622 ),
        new CharMetrics( 600, 0, 3, 0, 607, 562 ), new CharMetrics( 600, 0, 43, 0, 616, 562 ),
        new CharMetrics( 600, 0, 93, -18, 655, 580 ), new CharMetrics( 600, 0, 43, 0, 645, 562 ),
        new CharMetrics( 600, 0, 53, 0, 660, 562 ), new CharMetrics( 600, 0, 53, 0, 660, 562 ),
        new CharMetrics( 600, 0, 83, -18, 645, 580 ), new CharMetrics( 600, 0, 32, 0, 687, 562 ),
        new CharMetrics( 600, 0, 96, 0, 623, 562 ), new CharMetrics( 600, 0, 52, -18, 685, 562 ),
        new CharMetrics( 600, 0, 38, 0, 671, 562 ), new CharMetrics( 600, 0, 47, 0, 607, 562 ),
        new CharMetrics( 600, 0, 4, 0, 715, 562 ), new CharMetrics( 600, 0, 7, -13, 712, 562 ),
        new CharMetrics( 600, 0, 94, -18, 625, 580 ), new CharMetrics( 600, 0, 79, 0, 644, 562 ),
        new CharMetrics( 600, 0, 95, -138, 625, 580 ), new CharMetrics( 600, 0, 38, 0, 598, 562 ),
        new CharMetrics( 600, 0, 76, -20, 650, 580 ), new CharMetrics( 600, 0, 108, 0, 665, 562 ),
        new CharMetrics( 600, 0, 125, -18, 702, 562 ), new CharMetrics( 600, 0, 105, -13, 723, 562 ),
        new CharMetrics( 600, 0, 106, -13, 722, 562 ), new CharMetrics( 600, 0, 23, 0, 675, 562 ),
        new CharMetrics( 600, 0, 133, 0, 695, 562 ), new CharMetrics( 600, 0, 86, 0, 610, 562 ),
        new CharMetrics( 600, 0, 246, -108, 574, 622 ), new CharMetrics( 600, 0, 249, -80, 468, 629 ),
        new CharMetrics( 600, 0, 135, -108, 463, 622 ), new CharMetrics( 600, 0, 175, 354, 587, 622 ),
        new CharMetrics( 600, 0, -27, -125, 584, -75 ), new CharMetrics( 600, 0, 343, 328, 457, 562 ),
        new CharMetrics( 600, 0, 76, -15, 569, 441 ), new CharMetrics( 600, 0, 29, -15, 625, 629 ),
        new CharMetrics( 600, 0, 106, -15, 608, 441 ), new CharMetrics( 600, 0, 85, -15, 640, 629 ),
        new CharMetrics( 600, 0, 106, -15, 598, 441 ), new CharMetrics( 600, 0, 114, 0, 662, 629 ),
        new CharMetrics( 600, 0, 61, -157, 657, 441 ), new CharMetrics( 600, 0, 33, 0, 592, 629 ),
        new CharMetrics( 600, 0, 95, 0, 515, 657 ), new CharMetrics( 600, 0, 52, -157, 550, 657 ),
        new CharMetrics( 600, 0, 58, 0, 633, 629 ), new CharMetrics( 600, 0, 95, 0, 515, 629 ),
        new CharMetrics( 600, 0, -5, 0, 615, 441 ), new CharMetrics( 600, 0, 26, 0, 585, 441 ),
        new CharMetrics( 600, 0, 102, -15, 588, 441 ), new CharMetrics( 600, 0, -24, -157, 605, 441 ),
        new CharMetrics( 600, 0, 85, -157, 682, 441 ), new CharMetrics( 600, 0, 60, 0, 636, 441 ),
        new CharMetrics( 600, 0, 78, -15, 584, 441 ), new CharMetrics( 600, 0, 167, -15, 561, 561 ),
        new CharMetrics( 600, 0, 101, -15, 572, 426 ), new CharMetrics( 600, 0, 90, -10, 681, 426 ),
        new CharMetrics( 600, 0, 76, -10, 695, 426 ), new CharMetrics( 600, 0, 20, 0, 655, 426 ),
        new CharMetrics( 600, 0, -4, -157, 683, 426 ), new CharMetrics( 600, 0, 99, 0, 593, 426 ),
        new CharMetrics( 600, 0, 233, -108, 569, 622 ), new CharMetrics( 600, 0, 222, -250, 485, 750 ),
        new CharMetrics( 600, 0, 140, -108, 477, 622 ), new CharMetrics( 600, 0, 116, 197, 600, 320 ),
        new CharMetrics( 600, 0, 0, 0, 0, 0 ), new CharMetrics( 600, 0, 0, 0, 0, 0 ),
        new CharMetrics( 600, 0, 0, 0, 0, 0 ), new CharMetrics( 600, 0, 0, 0, 0, 0 ),
        new CharMetrics( 600, 0, 0, 0, 0, 0 ), new CharMetrics( 600, 0, 0, 0, 0, 0 ),
        new CharMetrics( 600, 0, 0, 0, 0, 0 ), new CharMetrics( 600, 0, 0, 0, 0, 0 ),
        new CharMetrics( 600, 0, 0, 0, 0, 0 ), new CharMetrics( 600, 0, 0, 0, 0, 0 ),
        new CharMetrics( 600, 0, 0, 0, 0, 0 ), new CharMetrics( 600, 0, 0, 0, 0, 0 ),
        new CharMetrics( 600, 0, 0, 0, 0, 0 ), new CharMetrics( 600, 0, 0, 0, 0, 0 ),
        new CharMetrics( 600, 0, 0, 0, 0, 0 ), new CharMetrics( 600, 0, 0, 0, 0, 0 ),
        new CharMetrics( 600, 0, 0, 0, 0, 0 ), new CharMetrics( 600, 0, 95, 0, 515, 426 ),
        new CharMetrics( 600, 0, 294, 497, 484, 672 ), new CharMetrics( 600, 0, 348, 497, 612, 672 ),
        new CharMetrics( 600, 0, 229, 477, 581, 654 ), new CharMetrics( 600, 0, 212, 489, 629, 606 ),
        new CharMetrics( 600, 0, 232, 525, 600, 565 ), new CharMetrics( 600, 0, 279, 501, 576, 609 ),
        new CharMetrics( 600, 0, 360, 477, 466, 580 ), new CharMetrics( 600, 0, 262, 492, 570, 595 ),
        new CharMetrics( 600, 0, 0, 0, 0, 0 ), new CharMetrics( 600, 0, 332, 463, 500, 627 ),
        new CharMetrics( 600, 0, 197, -151, 344, 10 ), new CharMetrics( 600, 0, 0, 0, 0, 0 ),
        new CharMetrics( 600, 0, 239, 497, 683, 672 ), new CharMetrics( 600, 0, 207, -151, 348, 0 ),
        new CharMetrics( 600, 0, 262, 492, 614, 669 ), new CharMetrics( 600, 0, 0, 0, 0, 0 ),
        new CharMetrics( 600, 0, 225, -157, 445, 430 ), new CharMetrics( 600, 0, 151, -49, 588, 614 ),
        new CharMetrics( 600, 0, 124, -21, 621, 611 ), new CharMetrics( 600, 0, 94, 58, 628, 506 ),
        new CharMetrics( 600, 0, 120, 0, 693, 562 ), new CharMetrics( 600, 0, 238, -175, 469, 675 ),
        new CharMetrics( 600, 0, 104, -78, 590, 580 ), new CharMetrics( 600, 0, 262, 492, 570, 595 ),
        new CharMetrics( 600, 0, 53, -18, 667, 580 ), new CharMetrics( 600, 0, 209, 249, 512, 580 ),
        new CharMetrics( 600, 0, 92, 70, 652, 446 ), new CharMetrics( 600, 0, 155, 108, 591, 369 ),
        new CharMetrics( 600, 0, 152, 231, 558, 285 ), new CharMetrics( 600, 0, 53, -18, 667, 580 ),
        new CharMetrics( 600, 0, 232, 525, 600, 565 ), new CharMetrics( 600, 0, 214, 269, 576, 622 ),
        new CharMetrics( 600, 0, 96, 44, 594, 558 ), new CharMetrics( 600, 0, 230, 249, 535, 622 ),
        new CharMetrics( 600, 0, 213, 240, 501, 622 ), new CharMetrics( 600, 0, 348, 497, 612, 672 ),
        new CharMetrics( 600, 0, 72, -157, 572, 426 ), new CharMetrics( 600, 0, 100, -78, 630, 562 ),
        new CharMetrics( 600, 0, 275, 189, 434, 327 ), new CharMetrics( 600, 0, 197, -151, 344, 10 ),
        new CharMetrics( 600, 0, 231, 249, 491, 622 ), new CharMetrics( 600, 0, 210, 249, 535, 580 ),
        new CharMetrics( 600, 0, 58, 70, 618, 446 ), new CharMetrics( 600, 0, 65, -57, 674, 665 ),
        new CharMetrics( 600, 0, 65, -57, 669, 665 ), new CharMetrics( 600, 0, 73, -56, 659, 666 ),
        new CharMetrics( 600, 0, 105, -157, 466, 430 ), new CharMetrics( 600, 0, 3, 0, 607, 793 ),
        new CharMetrics( 600, 0, 3, 0, 658, 793 ), new CharMetrics( 600, 0, 3, 0, 607, 775 ),
        new CharMetrics( 600, 0, 3, 0, 656, 732 ), new CharMetrics( 600, 0, 3, 0, 607, 731 ),
        new CharMetrics( 600, 0, 3, 0, 607, 753 ), new CharMetrics( 600, 0, 3, 0, 655, 562 ),
        new CharMetrics( 600, 0, 93, -151, 658, 580 ), new CharMetrics( 600, 0, 53, 0, 660, 793 ),
        new CharMetrics( 600, 0, 53, 0, 668, 793 ), new CharMetrics( 600, 0, 53, 0, 660, 775 ),
        new CharMetrics( 600, 0, 53, 0, 660, 731 ), new CharMetrics( 600, 0, 96, 0, 623, 793 ),
        new CharMetrics( 600, 0, 96, 0, 638, 793 ), new CharMetrics( 600, 0, 96, 0, 623, 775 ),
        new CharMetrics( 600, 0, 96, 0, 623, 731 ), new CharMetrics( 600, 0, 43, 0, 645, 562 ),
        new CharMetrics( 600, 0, 7, -13, 712, 732 ), new CharMetrics( 600, 0, 94, -18, 625, 793 ),
        new CharMetrics( 600, 0, 94, -18, 638, 793 ), new CharMetrics( 600, 0, 94, -18, 625, 775 ),
        new CharMetrics( 600, 0, 94, -18, 656, 732 ), new CharMetrics( 600, 0, 94, -18, 625, 731 ),
        new CharMetrics( 600, 0, 103, 43, 607, 470 ), new CharMetrics( 600, 0, 94, -80, 625, 629 ),
        new CharMetrics( 600, 0, 125, -18, 702, 793 ), new CharMetrics( 600, 0, 125, -18, 702, 793 ),
        new CharMetrics( 600, 0, 125, -18, 702, 775 ), new CharMetrics( 600, 0, 125, -18, 702, 731 ),
        new CharMetrics( 600, 0, 133, 0, 695, 793 ), new CharMetrics( 600, 0, 79, 0, 606, 562 ),
        new CharMetrics( 600, 0, 48, -15, 617, 629 ), new CharMetrics( 600, 0, 76, -15, 569, 672 ),
        new CharMetrics( 600, 0, 76, -15, 612, 672 ), new CharMetrics( 600, 0, 76, -15, 581, 654 ),
        new CharMetrics( 600, 0, 76, -15, 629, 606 ), new CharMetrics( 600, 0, 76, -15, 570, 595 ),
        new CharMetrics( 600, 0, 76, -15, 569, 627 ), new CharMetrics( 600, 0, 41, -15, 626, 441 ),
        new CharMetrics( 600, 0, 106, -151, 614, 441 ), new CharMetrics( 600, 0, 106, -15, 598, 672 ),
        new CharMetrics( 600, 0, 106, -15, 612, 672 ), new CharMetrics( 600, 0, 106, -15, 598, 654 ),
        new CharMetrics( 600, 0, 106, -15, 598, 595 ), new CharMetrics( 600, 0, 95, 0, 515, 672 ),
        new CharMetrics( 600, 0, 95, 0, 612, 672 ), new CharMetrics( 600, 0, 95, 0, 551, 654 ),
        new CharMetrics( 600, 0, 95, 0, 540, 595 ), new CharMetrics( 600, 0, 102, -15, 639, 629 ),
        new CharMetrics( 600, 0, 26, 0, 629, 606 ), new CharMetrics( 600, 0, 102, -15, 588, 672 ),
        new CharMetrics( 600, 0, 102, -15, 612, 672 ), new CharMetrics( 600, 0, 102, -15, 588, 654 ),
        new CharMetrics( 600, 0, 102, -15, 629, 606 ), new CharMetrics( 600, 0, 102, -15, 588, 595 ),
        new CharMetrics( 600, 0, 136, 48, 573, 467 ), new CharMetrics( 600, 0, 102, -80, 588, 506 ),
        new CharMetrics( 600, 0, 101, -15, 572, 672 ), new CharMetrics( 600, 0, 101, -15, 602, 672 ),
        new CharMetrics( 600, 0, 101, -15, 572, 654 ), new CharMetrics( 600, 0, 101, -15, 572, 595 ),
        new CharMetrics( 600, 0, -4, -157, 683, 672 ), new CharMetrics( 600, 0, -24, -157, 605, 629 ),
        new CharMetrics( 600, 0, -4, -157, 683, 595 )};

    MonospaceItalic()
    {
        super( true, 629, -157, new CharMetrics( 0, 0, -28, -250, 742, 805 ), METRICS );
    }
}
